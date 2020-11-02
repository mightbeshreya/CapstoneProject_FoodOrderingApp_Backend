package com.upgrad.FoodOrderingApp.api.controller;

// Address Controller Handles all  the Address related endpoints

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("")
@CrossOrigin
public class AddressController {

    @Autowired
    CustomerService customerService; // Handles all the Service Related to the Customer.

    @Autowired
    AddressService addressService; // Handles all the Service Related to the Address.

 /* The method handles Address save Related request.It takes the details as per in the SaveAddressRequest
     & produces response in SaveAddressResponse and returns UUID of newly Created Customer Address and Success message else Return error code and error Message.
      */

    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestBody(required = false) final SaveAddressRequest saveAddressRequest, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        //Access the accessToken from the request Header
        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];
        customerService.getCustomer(userAccessToken);

        //Creating addressEntity from SaveAddressRequest data.
        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setActive(1);
        System.out.println("saveAddressRequest state ID : " + saveAddressRequest.getStateUuid());
        StateEntity state = addressService.getStateByUUID(saveAddressRequest.getStateUuid());
        //addressEntity.setState(state);

        final AddressEntity createdAddress = addressService.saveAddress(addressEntity, state);

        //Creating SaveAddressResponse response
        final SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(createdAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(saveAddressResponse, HttpStatus.CREATED);
    }
}