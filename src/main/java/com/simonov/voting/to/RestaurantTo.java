package com.simonov.voting.to;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantTo extends NamedTo {

    @Schema(hidden = true)
    List<DishTo> dishes;

    public RestaurantTo(Integer id, String name, List<DishTo> dishes) {
        super(id, name);
        this.dishes = dishes;
    }
}