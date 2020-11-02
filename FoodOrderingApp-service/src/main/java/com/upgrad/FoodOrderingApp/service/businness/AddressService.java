package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/* Service class for Address Entity and customerAddress */
@Service
public class AddressService {

    @Autowired
    AddressDao addressDao; //Handle all data of Address

    @Autowired
    CustomerDao customerDao; //Handle all data customer

    @Autowired
    StateDao stateDao;

    @Autowired
    CustomerAddressDao customerAddressDao;

    /* This method is to saveAddress.Takes the Address and state entity and saves the Address to the DB.
        If error throws exception with error code and error message.
         */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity, StateEntity stateEntity) throws SaveAddressException, AuthorizationFailedException {
        /* If any field is empty throw exception */
        if (addressEntity.getCity().isEmpty() || addressEntity.getFlat_buil_number().isEmpty() || addressEntity.getPincode().isEmpty() || addressEntity.getLocality().isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        /* Check for PinCode Validation */
        if (!addressDao.IsPinCodeValid(addressEntity.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        /* If no exception,  Set state to Address Entity and Save in Database */
        addressEntity.setState(stateEntity);
        return addressDao.saveAddress(addressEntity);
    }

    //To get state by UUID
    public StateEntity getStateByUUID(String uuid) throws AddressNotFoundException {
        StateEntity stateEntity = stateDao.getStateByUuid(uuid);
        if (stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return stateEntity;
    }

    //To get Address By Customer
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) throws AuthorizationFailedException {
        // List of Customer Address - Adding into Address Entity List
        List<CustomerAddressEntity> customerAddressEntity = customerAddressDao.getAddressByCustomer(customerEntity);
        List<AddressEntity> addressEntityList = new ArrayList<>();
        for (CustomerAddressEntity cae : customerAddressEntity) {
            AddressEntity addressEntity = cae.getAddress();
            addressEntityList.add(addressEntity);
        }
        return addressEntityList;

    }

}