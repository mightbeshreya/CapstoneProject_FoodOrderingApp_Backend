package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

//import com.upgrad.FoodOrderingApp.service.businness.CouponService;

// Order Controller Handles all  the Order related endpoints
@RestController
@RequestMapping("")
@CrossOrigin
public class OrderController {

    @Autowired
    private PaymentService paymentService; // Handles all the Service Related Payment.

    @Autowired
    private OrderService orderService; // Handles all the Service Related Order.

    @Autowired
    private CustomerService customerService; // Handles all the Service Related Customer.

    @Autowired
    private AddressService addressService; // Handles all the Service Related Address.

    @Autowired
    private RestaurantService restaurantService; // Handles all the Service Related Restaurant.

    @Autowired
    private ItemService itemService; // Handles all the Service Related Item.

     /* The method handles get Coupon By CouponName request.It takes authorization from the header and coupon name as the path vataible.
    & produces response in CouponDetailsResponse and returns UUID,Coupon Name and Percentage of coupon present in the DB and if error returns error code and error Message.
    */
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader("authorization") final String authorization,
                                                                       @PathVariable("coupon_name") final String couponName)
                            throws AuthorizationFailedException, CouponNotFoundException {

        //Access the accessToken from the request Header
        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];

        //Calls customerService getCustomerMethod to check the validity of the customer.this methods returns the customerEntity.
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        //Calls getCouponByCouponName of orderService to get the coupon by name from DB
        CouponEntity couponEntity =  orderService.getCouponByCouponName(couponName);

        //Creating the couponDetailsResponse containing UUID,Coupon Name and percentage.
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName())
                .percent(couponEntity.getPercent());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

}
