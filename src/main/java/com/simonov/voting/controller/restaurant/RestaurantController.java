package com.simonov.voting.controller.restaurant;

import com.simonov.voting.error.IllegalRequestException;
import com.simonov.voting.model.Restaurant;
import com.simonov.voting.repository.RestaurantRepository;
import com.simonov.voting.service.ToMapper;
import com.simonov.voting.to.RestaurantTo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequestMapping(value = RestaurantController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Restaurant Controller")
@RestController
public class RestaurantController {

    static final String REST_URL = "/api/restaurants";

    private final RestaurantRepository repository;
    private final ToMapper toMapper;

    public RestaurantController(RestaurantRepository repository,
                                ToMapper toMapper) {
        this.repository = repository;
        this.toMapper = toMapper;
    }

    @GetMapping("/with-menu")
    @Cacheable(value = "restaurants", cacheManager = "dbCacheManager")
    public List<RestaurantTo> getAllWithTodayMenu() {
        log.info("Try getAllWithTodayMenu() Try restaurants with today menu");
        final List<RestaurantTo> result = toMapper.getRestaurantTos(repository.getAllWithMenu(LocalDate.now()));
        log.info("getAllWithTodayMenu() Found {}  restaurants with today menu", result.size());
        return result;
    }

    @GetMapping("/{id}/with-menu")
    @Cacheable(value = "restaurants")
    public RestaurantTo getByIdWithTodayMenu(@PathVariable int id) {
        log.info("Try getByIdWithTodayMenu() for {}", id);
        final Optional<Restaurant> withMenu = repository.getWithMenu(id, LocalDate.now());
        return withMenu.map(toMapper::createTo).orElseThrow(() ->
                new IllegalRequestException("Restaurant id: " + id + " not found"));
    }
}