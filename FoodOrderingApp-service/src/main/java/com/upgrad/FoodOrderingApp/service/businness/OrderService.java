package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* Service for Orders */
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Autowired
    CouponDao couponDao;

    @Autowired
    AddressDao addressDao;

    @Autowired
    PaymentDao paymentDao;

    @Autowired
    ItemDao itemDao;

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    CustomerAddressDao customerAddressDao;

    /* get Orders By customerUUID */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderEntity> getOrdersByCustomers(String customerUuid){

        CustomerEntity customerEntity = customerDao.getCustomerByUuid(customerUuid);

        List<OrderEntity> ordersEntities = orderDao.getOrdersByCustomers(customerEntity);
        return ordersEntities;
    }


    /* Get Order Items Order By */
    public List<OrderItemEntity> getOrderItemsByOrder(OrderEntity orderEntity) {
        List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrder(orderEntity);
        return orderItemEntities;
    }

    /* Save Order in Database */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(OrderEntity orderEntity) {
        return orderDao.saveOrder(orderEntity);
    }

    /* Save Order Item */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderedItem) {
        return orderDao.saveOrderItem(orderedItem);
    }

    /* get Coupon through Coupon UUID */
    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponId(final String couponUuid) throws CouponNotFoundException {
        CouponEntity couponEntity = couponDao.getCouponByUUID(couponUuid);
        if(couponEntity == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
        return couponEntity;
    }

    /* get Coupon By Coupon Name from DB */
    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponName(final String couponName) throws AuthorizationFailedException,
            CouponNotFoundException {
        if(couponName == null || couponName == "" || couponName.isEmpty()) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        CouponEntity couponEntity = couponDao.getCouponByName(couponName);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
        return couponEntity;
    }
}

