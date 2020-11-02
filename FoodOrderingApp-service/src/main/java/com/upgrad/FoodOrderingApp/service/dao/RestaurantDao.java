package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


//This Class is created to access DB with respect to Restaurant entity

@Repository
public class RestaurantDao {
    @PersistenceContext
    private EntityManager entityManager;


    //To get the list of restaurant by ratings from db
    public List<RestaurantEntity> restaurantsByRating() {
        try {
            List<RestaurantEntity> allRestaurants = entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class)
                    .getResultList();
            return allRestaurants;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RestaurantEntity> getAllRestaurants() {
        try {
            List<RestaurantEntity> allRestaurants = entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class)
                    .getResultList();
            return allRestaurants;
        } catch (NoResultException nre) {
            return null;
        }
    }


    //To get restaurant by UUID from db
    public RestaurantEntity getRestaurantByUuid(String uuid) {

        try {
            RestaurantEntity restaurantEntity = entityManager.createNamedQuery("getRestaurantByUuid", RestaurantEntity.class).setParameter("uuid", uuid).getSingleResult();
            return restaurantEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }


    //To get the list of restaurant by name from db
    public List<RestaurantEntity> restaurantsByName(String restaurantName) {
        try {
            String restaurantNameLow = "%" + restaurantName.toLowerCase() + "%";
            List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("restaurantByName", RestaurantEntity.class).setParameter("restaurant_name_lower", restaurantNameLow).getResultList();
            return restaurantEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }


    //To update the restaurant in the db and return updated restaurant entity.
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity) {
        return entityManager.merge(restaurantEntity);
    }


    //To update the restaurant details in the db and return updated restaurant entity.
    public RestaurantEntity updateRestaurantDetails(RestaurantEntity restaurantEntity) {
        return entityManager.merge(restaurantEntity);
    }


    //To get Auth token in the db and return accessToken.
    public CustomerAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("getToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
