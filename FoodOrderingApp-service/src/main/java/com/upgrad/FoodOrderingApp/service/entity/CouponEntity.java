package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "coupon")
@NamedQueries({
        @NamedQuery(name = "getCouponByCouponName", query = "SELECT c from CouponEntity c where c.couponName = :couponName"),
        @NamedQuery(name = "getCouponByCouponUuid", query = "SELECT c from CouponEntity c where c.uuid = :couponUuid")
})
public class CouponEntity implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "COUPON_NAME")
    @Size(max = 255)
    private String couponName;

    @Column(name = "PERCENT")
    @NotNull
    private Integer percent;

    public CouponEntity(){

    }

    public CouponEntity(String couponId, String myCoupon, int percent) {
        this.uuid = couponId;
        this.couponName = myCoupon;
        this.percent = percent;
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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

}
