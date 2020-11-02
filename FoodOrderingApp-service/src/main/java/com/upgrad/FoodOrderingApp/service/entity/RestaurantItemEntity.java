package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "restaurant_item")
@NamedQueries(
        {

        }
)
public class RestaurantItemEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "item_id")
    private com.upgrad.FoodOrderingApp.service.entity.ItemEntity item;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "restaurant_id")
    private com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity restaurant;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public com.upgrad.FoodOrderingApp.service.entity.ItemEntity getItem() {
        return item;
    }

    public void setItem(com.upgrad.FoodOrderingApp.service.entity.ItemEntity item) {
        this.item = item;
    }

    public com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }
}
