package com.upgrad.FoodOrderingApp.service.dao;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/* Interacting with Database Repository */
@Repository
public class StateDao {

    @PersistenceContext
    EntityManager entityManager;

    /* Get State Entity by state UUID */
    public StateEntity getStateByUuid(final String stateUuid) {
        try {
            return entityManager.createNamedQuery("getStateByUUID", StateEntity.class).setParameter("uuid", stateUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    /* Get all states in Database */
    public List<StateEntity> getAllStates() {
        try {
            return entityManager.createNamedQuery("getAllStates", StateEntity.class).getResultList();
            /*Query query = entityManager.createNamedQuery("getStateByUUID");
            return new ArrayList<StateEntity>(query.getResultList()); */
        } catch (NoResultException nre) {
            return null;
        }
    }

}