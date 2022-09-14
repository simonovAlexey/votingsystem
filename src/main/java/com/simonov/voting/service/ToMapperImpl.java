package com.simonov.voting.service;

import com.simonov.voting.model.*;
import com.simonov.voting.to.DishTo;
import com.simonov.voting.to.RestaurantTo;
import com.simonov.voting.to.UserTo;
import com.simonov.voting.to.VoteTo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ToMapperImpl implements ToMapper {

    public User createUserEntity(UserTo userTo) {
        return new User(userTo.getId(), userTo.getName(), userTo.getEmail().toLowerCase(), userTo.getPassword(), Role.USER);
    }

    @Override
    public DishTo createTo(Dish dish) {
        if (dish == null) {
            return null;
        }

        Integer id = dish.getId();
        String name = dish.getName();
        Integer price = dish.getPrice();

        return new DishTo(id, name, price);

    }

    @Override
    public List<DishTo> createDishTos(Collection<Dish> dishes) {
        if (dishes == null || dishes.isEmpty()) {
            return Collections.emptyList();
        }
        return dishes.stream().map(this::createTo).toList();
    }

    @Override
    public Dish createDishEntity(DishTo dishTo) {
        if (dishTo == null) {
            return null;
        }

        Integer id = dishTo.getId();
        String name = dishTo.getName();
        Integer price = dishTo.getPrice();

        return new Dish(id, name, price);

    }

    @Override
    public Restaurant createRestaurantEntityWithoutDishes(RestaurantTo to) {
        return new Restaurant(to.getId(), to.getName());
    }

    @Override
    public RestaurantTo createTo(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        Integer id = restaurant.getId();
        String name = restaurant.getName();
        List<DishTo> dishes = createDishTos(restaurant.getDishes());

        return new RestaurantTo(id, name, dishes);

    }

    @Override
    public List<RestaurantTo> getRestaurantTos(Collection<Restaurant> restaurants) {
        if (restaurants == null) {
            return Collections.emptyList();
        }

        List<RestaurantTo> list = new ArrayList<>(restaurants.size());
        for (Restaurant restaurant : restaurants) {
            list.add(createTo(restaurant));
        }

        return list;
    }

    @Override
    public VoteTo createTo(Vote vote) {
        if (vote == null) {
            return null;
        }

        LocalDate date = vote.getDate();
        int restaurantId = vote.getRestaurant().id();

        return new VoteTo(date, restaurantId);

    }

    @Override
    public List<VoteTo> createVoteTos(Collection<Vote> votes) {
        if (votes == null) {
            return Collections.emptyList();
        }
        return votes.stream().map(this::createTo).toList();
    }
}
