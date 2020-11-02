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

    /* The method handles past order request of customer.It takes authorization from the header
    & produces response in CustomerOrderResponse and returns details of all the past order arranged in date wise and if error returns error code and error Message.
    */
    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrdersOfUser(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, AuthorizationFailedException {


        //Access the accessToken from the request Header
        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];

        //Calls customerService getCustomerMethod to check the validity of the customer.this methods returns the customerEntity.
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        //Calls getOrdersByCustomers of orderService to get all the past orders of the customer.
        List<OrderEntity> ordersEntities = orderService.getOrdersByCustomers(customerEntity.getUuid());

        //Creating List of OrderList
        List<OrderList> orderLists = new LinkedList<>();

        if (ordersEntities != null) { //Checking if orderentities is null if yes them empty list is returned
            for (OrderEntity orderEntity : ordersEntities) { //looping in for every orderentity in orderentities

                //Calls getOrderItemsByOrder by order of orderService get all the items ordered in past by orders.
                List<OrderItemEntity> orderItemEntities = orderService.getOrderItemsByOrder(orderEntity);

                List<ItemQuantityResponse> itemQuantityResponseList = new LinkedList<>();
                orderItemEntities.forEach(orderItemEntity -> {
                    ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
                            .itemName(orderItemEntity.getItem().getItemName())
                            .itemPrice(orderItemEntity.getItem().getPrice())
                            .id(UUID.fromString(orderItemEntity.getItem().getUuid()))
                            .type(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().getValue()));
                    //Creating ItemQuantityResponse which will be added to the list
                    ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse()
                            .item(itemQuantityResponseItem).quantity(orderItemEntity.getQuantity())
                            .price(orderItemEntity.getPrice());
                    itemQuantityResponseList.add(itemQuantityResponse);
                });
                OrderListAddressState orderListAddressState = new OrderListAddressState()
                        .id(UUID.fromString(orderEntity.getAddress().getState().getUuid()))
                        .stateName(orderEntity.getAddress().getState().getStateName());

                OrderListAddress orderListAddress = new OrderListAddress().id(UUID.fromString(orderEntity.getAddress().getUuid()))
                        .flatBuildingName(orderEntity.getAddress().getFlat_buil_number())
                        .locality(orderEntity.getAddress().getLocality()).city(orderEntity.getAddress().getCity())
                        .pincode(orderEntity.getAddress().getPincode()).state(orderListAddressState);
                OrderListCoupon orderListCoupon = new OrderListCoupon().couponName(orderEntity.getCoupon().getCouponName())
                        .id(UUID.fromString(orderEntity.getCoupon().getUuid())).percent(orderEntity.getCoupon().getPercent());

                OrderListCustomer orderListCustomer = new OrderListCustomer()
                        .id(UUID.fromString(orderEntity.getCustomer().getUuid()))
                        .firstName(orderEntity.getCustomer().getFirstname())
                        .lastName(orderEntity.getCustomer().getLastname())
                        .emailAddress(orderEntity.getCustomer().getEmail())
                        .contactNumber(orderEntity.getCustomer().getContactNumber());

                OrderListPayment orderListPayment = new OrderListPayment()
                        .id(UUID.fromString(orderEntity.getPayment().getUuid()))
                        .paymentName(orderEntity.getPayment().getPaymentName());

                OrderList orderList = new OrderList().id(UUID.fromString(orderEntity.getUuid())).itemQuantities(itemQuantityResponseList)
                        .address(orderListAddress).bill(BigDecimal.valueOf(orderEntity.getBill()))
                        .date(String.valueOf(orderEntity.getDate())).discount(BigDecimal.valueOf(orderEntity.getDiscount()))
                        .coupon(orderListCoupon).customer(orderListCustomer).payment(orderListPayment);
                orderLists.add(orderList);
            }
            CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse()
                    .orders(orderLists);
            return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<CustomerOrderResponse>(new CustomerOrderResponse(), HttpStatus.OK);
        }

    }


}
