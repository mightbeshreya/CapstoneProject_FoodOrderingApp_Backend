package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;

@Repository
public class ItemDao {

    @PersistenceContext
    EntityManager entityManager;

    /* Search Item with item UUID */
    public ItemEntity getItemByUuid(String uuid){
        try {
            ItemEntity itemEntity = entityManager.createNamedQuery("itemByUuid", ItemEntity.class).setParameter("uuid", uuid).getSingleResult();
            return itemEntity;
        } catch (NoResultException nre){
            return null;
        }
    }

    /* Sorting */
    public Map<String,Integer> sortMapByValues(Map<String,Integer> map){

        List<Map.Entry<String,Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });

        Map<String, Integer> sortedByValueMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> item : list) {
            sortedByValueMap.put(item.getKey(), item.getValue());
        }

        return sortedByValueMap;
    }

}
