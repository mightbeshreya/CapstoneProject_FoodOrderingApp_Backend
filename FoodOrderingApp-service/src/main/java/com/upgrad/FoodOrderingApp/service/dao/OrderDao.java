package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    /* get Orders By Restaurant */
    public List<OrderEntity> getOrderByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<OrderEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByRestaurant", OrderEntity.class).setParameter("restaurant", restaurantEntity).getResultList();
            return ordersEntities;
        } catch (NoResultException nre){
            return null;
        }
    }

    /* Get ORders By Customers */
    public List<OrderEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
        try {
            List<OrderEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByCustomer", OrderEntity.class).setParameter("customer", customerEntity).getResultList();
            return ordersEntities;
        } catch (NoResultException nre){
            return null;
        }
    }

    /* Persist Order */
    public OrderEntity saveOrder(OrderEntity orderEntity) {
        try {
            entityManager.persist(orderEntity);
            return orderEntity;
        }catch (Exception e) {
            return null;
        }
    }

    /* Persist Order Item  */
    public OrderItemEntity saveOrderItem(OrderItemEntity orderedItem) {
        try {
            entityManager.persist(orderedItem);
            return orderedItem;
        }catch(Exception e) {
            return null;
        }
    }
}
