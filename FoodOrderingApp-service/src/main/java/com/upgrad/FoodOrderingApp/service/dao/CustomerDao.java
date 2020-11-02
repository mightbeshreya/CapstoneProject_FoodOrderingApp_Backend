package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

//This Class is created to access DB with respect to CustomerAddress entity

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To Create Customer.
    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    //To check contact no. is exist
    public CustomerEntity IsContactNumberExists(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class).setParameter("contactNumber", contactNumber).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }


    //To create Authority token
    public CustomerAuthEntity createAuthToken(CustomerAuthEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
         return customerAuthTokenEntity;
    }
    //To update customer log out
    public void updateCustomerLogoutAt(final CustomerAuthEntity customerAuthToken) {
        entityManager.merge(customerAuthToken);
    }
    // To get user auth Token
    public CustomerAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    //To check auth token
    public CustomerAuthEntity checkAuthToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("getToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    //To update customer datils
    public CustomerEntity updateCustomerDetails(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
        return customerEntity;
    }

    public CustomerEntity updatePassword(CustomerEntity updatedCustomerPassword) {
        entityManager.merge(updatedCustomerPassword);
        return updatedCustomerPassword;
    }

    //To get customer by Uuid
    public CustomerEntity getCustomerByUuid(String customerUuid) {
        try {
            CustomerEntity customer = entityManager.createNamedQuery("userByUuid", CustomerEntity.class).setParameter("uuid", customerUuid).getSingleResult();
            return customer;
        } catch (NoResultException nre) {
            return null;
        }
    }

}
