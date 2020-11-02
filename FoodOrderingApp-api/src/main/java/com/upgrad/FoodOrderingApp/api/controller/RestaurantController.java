package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

// Restaurant Controller Handles all  the restaurant related endpoints

@RestController
@RequestMapping("")
@CrossOrigin
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService; // Handles all the Service Related to Restaurant.

    @Autowired
    private AddressService addressService; // Handles all the Service Related to Address.

    @Autowired
    CategoryService categoryService;  // Handles all the Service Related to category.

    @Autowired
    ItemService itemService;  // Handles all the Service Related to Item.


    @Autowired
    CustomerService customerService;  // Handles all the Service Related to Customer.

    /* The method handles get All Restaurants request
    & produces response in RestaurantListResponse and returns list of restaurant with details from the db. If error returns error code and error message.
    */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

        //Calls restaurantsByRating method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> listOfRestaurants = restaurantService.restaurantsByRating();

        //Creating restaurant list for the response
        List<RestaurantList> restaurantList = new ArrayList<>();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity restaurantEntity : listOfRestaurants) { //Looping for each restaurant entity in restaurantEntities
            RestaurantList restaurantDetails = new RestaurantList();
            restaurantDetails.setId(UUID.fromString(restaurantEntity.getUuid()));
            restaurantDetails.setRestaurantName(restaurantEntity.getRestaurantName());
            restaurantDetails.setPhotoURL(restaurantEntity.getPhotoUrl());
            restaurantDetails.setCustomerRating(new BigDecimal(Double.toString(restaurantEntity.getCustomerRating())));
            restaurantDetails.setAveragePrice(restaurantEntity.getAvgPriceForTwo());
            restaurantDetails.setNumberCustomersRated(restaurantEntity.getNumOfCustomersRated());

            addressService.getAddressById(restaurantEntity.getAddress().getUuid());

            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
            responseAddress.setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
            responseAddress.setFlatBuildingName(restaurantEntity.getAddress().getFlat_buil_number());
            responseAddress.setLocality(restaurantEntity.getAddress().getLocality());
            responseAddress.setCity(restaurantEntity.getAddress().getCity());
            responseAddress.setPincode(restaurantEntity.getAddress().getPincode());

            //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();
            responseAddressState.setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
            responseAddressState.setStateName(restaurantEntity.getAddress().getState().getStateName());

            responseAddress.setState(responseAddressState);

            restaurantDetails.setAddress(responseAddress);

            List<CategoryEntity> categoryEntityList = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            List<String> stringCategories = new ArrayList<>();
            for (CategoryEntity category : categoryEntityList) {
                stringCategories.add(category.getCategoryName());
            }
            Collections.sort(stringCategories);

            String categoryString = String.join(", ", stringCategories);
            restaurantDetails.setCategories(categoryString);

            //Adding it to the list
            restaurantList.add(restaurantDetails);
        }

        //Creating the RestaurantListResponse by adding the list of RestaurantList
        restaurantListResponse.setRestaurants(restaurantList);
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

    /* The method handles get Restaurant By Restaurant Id. It takes restaurant_id as the path variable.
    & produces response in RestaurantDetailsResponse and returns details of restaurant from the db. If error returns error code and error message.
    */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantID(
            @PathVariable("restaurant_id") final String restaurantUuid)
            throws RestaurantNotFoundException {

        //Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
        List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantUuid);

        List<CategoryList> categoryLists = new LinkedList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {

            List<ItemEntity> itemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantUuid, categoryEntity.getUuid());
            List<ItemList> itemLists = new LinkedList<>();
            itemEntities.forEach(itemEntity -> {
                ItemList itemList = new ItemList().id(UUID.fromString(itemEntity.getUuid()))
                        .itemName(itemEntity.getItemName()).price(itemEntity.getPrice())
                        .itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType().getValue()));

                itemLists.add(itemList);
            });
            CategoryList categoryList = new CategoryList()
                    .itemList(itemLists)
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());

            categoryLists.add(categoryList);
        }

        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState();
        restaurantDetailsResponseAddressState.setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
        restaurantDetailsResponseAddressState.setStateName(restaurantEntity.getAddress().getState().getStateName());

        //Creating the RestaurantDetailsResponseAddress for the RestaurantList
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                .city(restaurantEntity.getAddress().getCity())
                .flatBuildingName(restaurantEntity.getAddress().getFlat_buil_number())
                .locality(restaurantEntity.getAddress().getLocality())
                .pincode(restaurantEntity.getAddress().getPincode())
                .state(restaurantDetailsResponseAddressState);

        //Creating the RestaurantDetailsResponse by adding the list of categoryList and other details.
        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
                .restaurantName(restaurantEntity.getRestaurantName())
                .address(restaurantDetailsResponseAddress)
                .averagePrice(restaurantEntity.getAvgPriceForTwo())
                .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                .numberCustomersRated(restaurantEntity.getNumOfCustomersRated())
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .photoURL(restaurantEntity.getPhotoUrl())
                .categories(categoryLists);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }

    /* The method handles update Restaurant Details. It takes restaurant_id as the path variable  and authorization in header and also customer rating.
     & produces response in RestaurantUpdatedResponse and returns UUID of Updated restaurant from the db and successful message. If error returns error code and error message.
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestParam(name = "customer_rating") Double customerRating,
                                                                             @PathVariable("restaurant_id") final String restaurant_id,
                                                                             @RequestHeader("authorization") final String authorization)
            throws RestaurantNotFoundException, InvalidRatingException, AuthorizationFailedException {

        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];
        customerService.getCustomer(accessToken);
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurant_id);
        restaurantEntity.setUuid(restaurant_id);

        restaurantService.updateRestaurantRating(restaurantEntity, customerRating);
        RestaurantUpdatedResponse restaurantUpdatedResponse =
                new RestaurantUpdatedResponse()
                        .id(UUID.fromString(restaurant_id))
                        .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    /* The method handles get Restaurant By Name. It takes Restaurant name as the path variable.
    & produces response in RestaurantListResponse and returns list of restaurant with details from the db. If error returns error code and error message.
    */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{reastaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurantByName(@PathVariable("reastaurant_name") final String restaurantName) throws RestaurantNotFoundException {

        //Calls restaurantsByName method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByName(restaurantName);
        if (!restaurantEntities.isEmpty()) {
            List<RestaurantList> restaurantLists = new LinkedList<>();
            for (RestaurantEntity restaurantEntity : restaurantEntities) {
                List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
                String categories = new String();
                ListIterator<CategoryEntity> listIterator = categoryEntities.listIterator();

                while (listIterator.hasNext()) {
                    categories = categories + listIterator.next().getCategoryName();
                    if (listIterator.hasNext()) {
                        categories = categories + ", ";
                    }
                }
                addressService.getAddressById(restaurantEntity.getAddress().getUuid());

                RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
                responseAddress.setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
                responseAddress.setFlatBuildingName(restaurantEntity.getAddress().getFlat_buil_number());
                responseAddress.setLocality(restaurantEntity.getAddress().getLocality());
                responseAddress.setCity(restaurantEntity.getAddress().getCity());
                responseAddress.setPincode(restaurantEntity.getAddress().getPincode());

                RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState();
                state.setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
                state.setStateName(restaurantEntity.getAddress().getState().getStateName());
                responseAddress.setState(state);

                RestaurantList restaurantDetails = new RestaurantList();
                restaurantDetails.setId(UUID.fromString(restaurantEntity.getUuid()));
                restaurantDetails.setRestaurantName(restaurantEntity.getRestaurantName());
                restaurantDetails.setPhotoURL(restaurantEntity.getPhotoUrl());
                restaurantDetails.setCustomerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
                restaurantDetails.setAveragePrice(restaurantEntity.getAvgPriceForTwo());
                restaurantDetails.setNumberCustomersRated(restaurantEntity.getNumOfCustomersRated());
                restaurantDetails.setAddress(responseAddress);
                restaurantDetails.setCategories(categories);

                restaurantLists.add(restaurantDetails);
            }

            RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
            return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new RestaurantListResponse(), HttpStatus.OK);
        }
    }

    /* The method handles get Restaurant By Category Id. It takes category_id as the path variable.
    & produces response in RestaurantListResponse and returns list of restaurant with details from the db. If error returns error code and error message.
    */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategoryId(
            @PathVariable(value = "category_id") final String categoryId) throws CategoryNotFoundException {

        //Calls restaurantByCategory method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> listOfCategoryRestaurants = restaurantService.restaurantByCategory(categoryId);

        List<RestaurantList> restaurantList = new ArrayList<RestaurantList>();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity restaurantEntity : listOfCategoryRestaurants) {
            RestaurantList restaurantDetails = new RestaurantList();
            restaurantDetails.setId(UUID.fromString(restaurantEntity.getUuid()));
            restaurantDetails.setRestaurantName(restaurantEntity.getRestaurantName());
            restaurantDetails.setPhotoURL(restaurantEntity.getPhotoUrl());
            restaurantDetails.setCustomerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
            restaurantDetails.setAveragePrice(restaurantEntity.getAvgPriceForTwo());
            restaurantDetails.setNumberCustomersRated(restaurantEntity.getNumOfCustomersRated());

            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
            responseAddress.setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
            responseAddress.setFlatBuildingName(restaurantEntity.getAddress().getFlat_buil_number());
            responseAddress.setLocality(restaurantEntity.getAddress().getLocality());
            responseAddress.setCity(restaurantEntity.getAddress().getCity());
            responseAddress.setPincode(restaurantEntity.getAddress().getPincode());

            //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();
            responseAddressState.setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
            responseAddressState.setStateName(restaurantEntity.getAddress().getState().getStateName());

            responseAddress.setState(responseAddressState);

            restaurantDetails.setAddress(responseAddress);

            List<CategoryEntity> restaurantCategories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            List<String> stringCategories = new ArrayList<>();
            for (CategoryEntity c : restaurantCategories) {
                stringCategories.add(c.getCategoryName());
            }
            Collections.sort(stringCategories);

            restaurantDetails.setCategories(String.join(", ", stringCategories));

            //restaurantListResponse.addRestaurantsItem(restaurantDetails);
            restaurantList.add(restaurantDetails);
        }
        restaurantListResponse.setRestaurants(restaurantList);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);

    }

}
