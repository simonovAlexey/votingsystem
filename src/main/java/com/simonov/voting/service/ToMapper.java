package com.simonov.voting.service;

import com.simonov.voting.model.Dish;
import com.simonov.voting.model.Restaurant;
import com.simonov.voting.model.User;
import com.simonov.voting.model.Vote;
import com.simonov.voting.to.DishTo;
import com.simonov.voting.to.RestaurantTo;
import com.simonov.voting.to.UserTo;
import com.simonov.voting.to.VoteTo;

import java.util.Collection;
import java.util.List;

public interface ToMapper {

    User createUserEntity(UserTo userTo);


    Dish createDishEntity(DishTo dishTo);

    DishTo createTo(Dish dish);

    List<DishTo> createDishTos(Collection<Dish> dishes);


    RestaurantTo createTo(Restaurant restaurant);

    Restaurant createRestaurantEntityWithoutDishes(RestaurantTo restaurantTo);

    List<RestaurantTo> getRestaurantTos(Collection<Restaurant> restaurants);


    VoteTo createTo(Vote vote);

    List<VoteTo> createVoteTos(Collection<Vote> votes);

}
