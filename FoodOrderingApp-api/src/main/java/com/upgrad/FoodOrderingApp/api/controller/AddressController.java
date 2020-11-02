package com.upgrad.FoodOrderingApp.api.controller;

// Address Controller Handles all  the Address related endpoints

import com.upgrad.FoodOrderingApp.api.model.*;
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

import java.util.ArrayList;
import java.util.List;
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

     /*  The method handles get all Address  request.It takes the authorization
         & produces response in AddressListResponse and returns list of Customer Address .If error Return error code and error Message.
          */

    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddress(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        //Access the accessToken from the request Header
        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];

        //Calls customerService getCustomer Method to check the validity of the customer.this methods returns the customerEntity  to be get all address.
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        List<AddressEntity> sortedAddress = addressService.getAllAddress(customerEntity);

        //List<CustomerAddressEntity> customerAddressEntityList = addressService.getAllAddress(bearerToken[1]);
        AddressListResponse addressListResponses = new AddressListResponse();

        List<AddressList> addressesList = new ArrayList<>();

        //Looping in for each address in the addressEntities & then Created AddressList using the each address data and adds to addressLists.
        for(AddressEntity addressEntity: sortedAddress) {
            AddressListState addressListState = new AddressListState().id(UUID.fromString(addressEntity.getState().getUuid())).stateName(addressEntity.getState().getStateName());
            AddressList addressList = new AddressList().id(UUID.fromString(addressEntity.getUuid()))
                    .flatBuildingName(addressEntity.getFlat_buil_number()).locality(addressEntity.getLocality())
                    .city(addressEntity.getCity()).pincode(addressEntity.getPincode()).state(addressListState);
            //AddressListResponse addressListResponse = new AddressListResponse().addAddressesItem(addressList);
            addressesList.add(addressList);
        }
        addressListResponses.addresses(addressesList);

        return new ResponseEntity<AddressListResponse>(addressListResponses, HttpStatus.OK);
    }
 /*  The method handles delete  Address  request.It takes the authorization and path variables address UUID
  & produces response in DeleteAddressResponse and returns UUID of deleted address and Successfull message .If error Return error code and error Message.
   */

    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@RequestHeader("authorization") final String authorization,
                                                                    @PathVariable(value = "address_id") final String addressUuid)
            throws AuthorizationFailedException, AddressNotFoundException {

        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        AddressEntity addressEntity = addressService.getAddressByUUID(addressUuid, customerEntity);

        AddressEntity deleteAddress = addressService.deleteAddress(addressEntity);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deleteAddress.getUuid())).status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);
    }

    /*  The method handles States request.It produces response in StatesListResponse and returns UUID & stateName .If error Return error code and error Message.
     */

    @RequestMapping(method = RequestMethod.GET, path = "/states", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {
        List<StateEntity> stateEntities = addressService.getAllStates();

        if(stateEntities.isEmpty()) {
            return  new ResponseEntity<StatesListResponse>(new StatesListResponse(), HttpStatus.OK);
        }

        List<StatesList> states = new ArrayList<>();
        for (StateEntity state : stateEntities) {
            UUID stateUuid = UUID.fromString(state.getUuid());
            StatesList statesList = new StatesList().id(stateUuid).stateName(state.getStateName());
            //StatesListResponse statesListResponse = new StatesListResponse().addStatesItem(statesList);
            states.add(statesList);
        }

        StatesListResponse statesListResponse = new StatesListResponse().states(states);
        return  new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
    }

}

