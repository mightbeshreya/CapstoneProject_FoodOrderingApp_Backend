package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//This Class handles all service related to the Payment

@Service
public class PaymentService {
    @Autowired
    private PaymentDao paymentDao; //Handles all data related to the PaymentEntity.

     /* This method is to get All Payment Methods.Takes the couponName  and returns the PaymentEntity.
    If error throws exception with error code and error message.
    */
    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getPaymentMethods();
    }

    /* This method is to get Payment By UUID.Takes the paymentId  and returns the PaymentEntity.
    If error throws exception with error code and error message.
    */
    public PaymentEntity getPaymentByUUID(final String paymentUuid) throws PaymentMethodNotFoundException {
        PaymentEntity paymentEntity = paymentDao.getPaymentMethodByUUID(paymentUuid);
        if(paymentEntity==null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        return paymentEntity;
    }
}
