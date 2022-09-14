package com.simonov.voting.controller.restaurant;

import com.simonov.voting.controller.MatcherFactory;
import com.simonov.voting.model.Restaurant;
import com.simonov.voting.to.RestaurantTo;

import static com.simonov.voting.controller.MatcherFactory.usingEqualsComparator;
import static com.simonov.voting.controller.dish.TestDataDish.*;

public class TestDataRestaurant {

    public static final int REST_1_ID = 1;
    public static final int REST_2_ID = 2;
    public static final int REST_3_ID = 3;
    public static final int REST_NOT_FOUND_ID = 66;

    public static final Restaurant CH_RESTAUR_1 = new Restaurant(REST_1_ID, "1чебуречная");
    public static final Restaurant RUM_RESTAUR_2 = new Restaurant(REST_2_ID, "2рюмочная");
    public static final Restaurant BLIN_RESTAUR_3 = new Restaurant(REST_3_ID, "3блинная");

    public static final MatcherFactory.Matcher<Restaurant> RESTAUR_MATCHER = usingEqualsComparator(Restaurant.class);
    public static final MatcherFactory.Matcher<RestaurantTo> RESTAUR_TO_MATCHER = usingEqualsComparator(RestaurantTo.class);

    static {
        CH_RESTAUR_1.setDishes(chebTodayMenu);
        RUM_RESTAUR_2.setDishes(rumTodayMenu);
        BLIN_RESTAUR_3.setDishes(blinTodayMenu);
    }
}