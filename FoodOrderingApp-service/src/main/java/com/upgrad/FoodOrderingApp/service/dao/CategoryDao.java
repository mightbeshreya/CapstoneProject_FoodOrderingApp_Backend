package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/* Interact with Database Repository */
@Repository
public class CategoryDao {
    @PersistenceContext
    private EntityManager entityManager;

    /* Search for Category by Category ID */
    public CategoryEntity getCategoryById(String categoryId) {
        try {
            return entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class)
                    .setParameter("categoryId", categoryId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* Search for Category by Category UUID */
    public CategoryEntity getCategoryByUuid(String uuid) {
        try {
            CategoryEntity categoryEntity = entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class).setParameter("categoryId", uuid).getSingleResult();
            return categoryEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* Get Items in Category */
    public List<CategoryItemEntity> getItemByCategory(CategoryEntity categoryEntity) {
        try {
            List<CategoryItemEntity> categoryItemEntities = entityManager.createNamedQuery("getItemsByCategory", CategoryItemEntity.class).setParameter("category", categoryEntity).getResultList();
            return categoryItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* Get All Categories Ordered by Category Names */
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        try {
            List<CategoryEntity> categoryEntities = entityManager.createNamedQuery("getAllCategoriesOrderedByName", CategoryEntity.class).getResultList();
            return categoryEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
