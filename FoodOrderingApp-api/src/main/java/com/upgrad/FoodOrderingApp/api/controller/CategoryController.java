package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/*This is a Rest Controller Which handles two endpoints :
1. Get All Categories - “/category” : Getting All the categories from the database.
It is a GET request and will not require any parameters from the user.
2.Get Category by Id - “/category/{category_id}”
It is a GET Request and takes Category UUID in category_id parameter as path variable
 */
@RestController
@RequestMapping("")
@CrossOrigin
public class CategoryController {

    /*Performing Services through Category Service */
    @Autowired
    private CategoryService categoryService;

    /* GET ALL Categories present in Database */
    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {
        /* All categories are ordered by Name and called through service */
        List<CategoryEntity> categoryEntities = categoryService.getAllCategoriesOrderedByName();

        /* Categories List Not empty from Database */
        if (!categoryEntities.isEmpty()) {
            //Adding into Category List Responses each Category List Response
            List<CategoryListResponse> categoryListResponses = new LinkedList<>();
            //Mapping Category Entity to Category List Response
            categoryEntities.forEach(categoryEntity -> {
                CategoryListResponse categoryListResponse = new CategoryListResponse()
                        .id(UUID.fromString(categoryEntity.getUuid()))
                        .categoryName(categoryEntity.getCategoryName());
                categoryListResponses.add(categoryListResponse);
            });
            //Returning Category List Response
            CategoriesListResponse categoriesListResponse = new CategoriesListResponse().categories(categoryListResponses);
            return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
        } else {
            //If Category List In Database is Empty, returning Empty CategoriesListResponse
            return new ResponseEntity<CategoriesListResponse>(new CategoriesListResponse(), HttpStatus.OK);
        }
    }



}
