package com.simonov.voting.controller.restaurant;

import com.simonov.voting.error.IllegalRequestException;
import com.simonov.voting.model.Restaurant;
import com.simonov.voting.repository.DishRepository;
import com.simonov.voting.repository.RestaurantRepository;
import com.simonov.voting.service.ToMapper;
import com.simonov.voting.to.RestaurantTo;
import com.simonov.voting.util.validation.ValidationUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@AllArgsConstructor
@CacheConfig(cacheNames = {"restaurants"})
@Tag(name = "Restaurant controller for admin auth")
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = APPLICATION_JSON_VALUE)
public class AdminRestaurantController {

    static final String REST_URL = "/api/admin/restaurants";

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    private final ToMapper toMapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @CacheEvict(allEntries = true)
    public ResponseEntity<Restaurant> create(@Valid @RequestBody RestaurantTo restaurant) {
        log.info("Try create() {}", restaurant);
        ValidationUtil.checkNew(restaurant);
        Restaurant created = restaurantRepository.save(toMapper.createRestaurantEntityWithoutDishes(restaurant));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        log.info("Try get restaurant {}", id);
        return restaurantRepository.findById(id).orElseThrow(
                () -> new IllegalRequestException("Restaurant id:" + id + " not found"));
    }

    @GetMapping
    public List<Restaurant> getAll() {
        log.info("Try getAll() restaurants.");
        return restaurantRepository.findAll(Sort.by(ASC, "name"));
    }


    @CacheEvict(allEntries = true)
    @ResponseStatus(NO_CONTENT)
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public void update(@Valid @RequestBody RestaurantTo restaurantTo, @PathVariable int id) {
        log.info("Try update() restaurant id: {}", id);
        ValidationUtil.checkAndSetHasId(restaurantTo, id);
        restaurantRepository.findById(id).orElseThrow(() ->
                new IllegalRequestException("Restaurant with id=" + id + " not found"));

        restaurantRepository.save(toMapper.createRestaurantEntityWithoutDishes(restaurantTo));
        log.info("updated: {}", restaurantTo);
    }

    @CacheEvict(allEntries = true)
    @ResponseStatus(NO_CONTENT)
    @Transactional
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Try delete restaurant: {}", id);

        restaurantRepository.findById(id).orElseThrow(() ->
                new IllegalRequestException("Restaurant id: " + id + " not found"));

        if (!dishRepository.getAll(id).stream().allMatch(dish -> dish.getDate().equals(LocalDate.now()))) {
            throw new IllegalRequestException("Can't delete restaurant that already has menu");
        }
        restaurantRepository.deleteIfExist(id);
    }


}