package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "payment")
@NamedQueries({
        @NamedQuery(name = "getAllPaymentMethods", query = "SELECT p from PaymentEntity p"),
        @NamedQuery(name = "getPaymentMethodByUuid", query = "SELECT p from PaymentEntity p where p.uuid = :paymentUuid")
})
public class PaymentEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "PAYMENT_NAME")
    @Size(max = 255)
    private String paymentName;


    public PaymentEntity() {

    }

    public PaymentEntity(String uuid, String paymentName) {
        this.uuid = uuid;
        this.paymentName = paymentName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}

