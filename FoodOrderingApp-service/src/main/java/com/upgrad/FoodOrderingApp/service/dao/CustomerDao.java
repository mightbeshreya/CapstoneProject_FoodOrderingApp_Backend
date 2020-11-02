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
}