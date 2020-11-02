package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("")
@CrossOrigin
public class CustomerController {
    /*  AutoWiring customer service */
    @Autowired
    CustomerService customerService;

    /* The method handles Customer SignUp Related request.It takes the details as per in the SignupCustomerRequest
    & produces response in SignupCustomerResponse and returns UUID of newly Created Customer and Success message else Return error code and error Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        /* If any field other than Last name is Null, throwing Exception SGR-005 */
        if (signupCustomerRequest.getFirstName() == null
                || signupCustomerRequest.getEmailAddress() == null
                || signupCustomerRequest.getPassword() == null
                || signupCustomerRequest.getContactNumber() == null
                || signupCustomerRequest.getFirstName().isEmpty()
                || signupCustomerRequest.getEmailAddress().isEmpty()
                || signupCustomerRequest.getPassword().isEmpty()
                || signupCustomerRequest.getContactNumber().isEmpty()) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        /* Creating New Customer Entity and saving in Database and sending created Customer UUID as
        response and returning response
         */
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        CustomerEntity createdCustomer = customerService.saveCustomer(customerEntity);

        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(createdCustomer.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }


    /*This Method handles the Login request and takes authorization parameter in Base64 coded and produces a LoginResponse containing info customer
    and response header containing bearer accessToken. If error returns the error code with corresponding Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        /* Decoding the authorization token to get Access Token and UserName and Password */
        authFormatCheck(authorization);
        byte[] decode = Base64.getDecoder().decode(authorization.split(" ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        /* Getting Customer from Customer Auth Token if Customer Exists, setting header with access token and
         * sending login response
         *  */
        CustomerAuthEntity customerAuthTokenEntity = customerService.authenticate(decodedArray[0], decodedArray[1]);
        CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();
        LoginResponse loginResponse = new LoginResponse().id(customerEntity.getUuid()).message("LOGGED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthTokenEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    /* This Method Performs check whether Authorization is in correct format or not - Called In Login*/
    public void authFormatCheck(final String authorization) throws AuthenticationFailedException {
        try {
            byte[] decoded = Base64.getDecoder().decode(authorization.split(" ")[1]);
            String decodedText = new String(decoded);
            String[] decodedArray = decodedText.split(":");
            if (authorization != null && authorization.startsWith("Basic ") && decodedArray.length == 2) {
                return;
            } else {
                throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
            }
        } catch (Exception e) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
    }
    /* This method handles the customer logout ,It takes the Bearer accessToken from authorization in the header and logs out the customer
    and returns a LogoutResponse conatining UUID of customer and the successful message.If error returns the error code with corresponding Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        /* Decoding Authorization to get access token and username and password */
        String[] authorizationData = authorization.split(" ");
        String accessToken = authorizationData[1];
        /* Sending Access Token For Logging Out  */
        CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
        customerAuthEntity = customerService.logout(accessToken);
        CustomerEntity customer = customerAuthEntity.getCustomer();
        /* Sending Logout Response */
        LogoutResponse authorizedLogoutResponse = new LogoutResponse().id(customer.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(authorizedLogoutResponse,  HttpStatus.OK);
    }
}