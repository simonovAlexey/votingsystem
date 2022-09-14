package com.simonov.voting.controller.dish;

import com.simonov.voting.error.IllegalRequestException;
import com.simonov.voting.model.Dish;
import com.simonov.voting.repository.DishRepository;
import com.simonov.voting.repository.RestaurantRepository;
import com.simonov.voting.service.ToMapper;
import com.simonov.voting.to.DishTo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.simonov.voting.util.validation.ValidationUtil.checkAndSetHasId;
import static com.simonov.voting.util.validation.ValidationUtil.checkNew;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@Slf4j
@RequestMapping(value = DishController.REST_URL, produces = APPLICATION_JSON_VALUE)
@CacheConfig(cacheNames = {"restaurants"})
@Tag(name = "Dish controller")
@RestController
public class DishController {

    static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;
    private final ToMapper commonMapper;

    public DishController(DishRepository dishRepository,
                          RestaurantRepository restaurantRepository,
                          ToMapper commonMapper) {
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
        this.commonMapper = commonMapper;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @CacheEvict(allEntries = true)
    @Transactional
    public ResponseEntity<Dish> create(@Valid @RequestBody DishTo dishTo,
                                       @PathVariable int restaurantId) {
        log.info("create() restaurant: {}  dish {} ", restaurantId, dishTo);
        checkNew(dishTo);

        Dish savedDish = dishRepository.save(
                commonMapper.createDishEntity(dishTo)
                        .setRestaurant(restaurantRepository.getReferenceById(restaurantId))
                        .setDate(LocalDate.now()));

        URI uri = fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, savedDish.getId()).toUri();
        return ResponseEntity.created(uri).body(savedDish);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Transactional
    public void update(@Valid @RequestBody DishTo dishTo,
                       @PathVariable int id,
                       @PathVariable int restaurantId) {
        log.info("update()  restaurant: {} dish: {} ", restaurantId, id);
        checkAndSetHasId(dishTo, id);
        dishRepository.checkExistsAndGet(id, restaurantId);
        final Dish entity = commonMapper.createDishEntity(dishTo)
                .setRestaurant(restaurantRepository.getReferenceById(restaurantId))
                .setDate(LocalDate.now());
         dishRepository.save(entity);
    }

    @GetMapping("/{id}")
    public Dish get(@PathVariable int id,
                    @PathVariable int restaurantId) {
        log.info("Try get dish {} for restaurant {}", id, restaurantId);
        return dishRepository.checkExistsAndGet(id, restaurantId);
    }

    @GetMapping("/date")
    public List<DishTo> getAllByRestaurantAndDate(@PathVariable int restaurantId,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate date) {
        log.info("getAllByRestaurantAndDate() restaurant: {}  date: {}", restaurantId, date);
        return commonMapper.createDishTos(dishRepository.getAll(restaurantId, date == null ? LocalDate.now() : date));
    }

    @GetMapping
    public List<Dish> getAllByRestaurant(@PathVariable int restaurantId) {
        log.info("getAllByRestaurant() restaurant: {}", restaurantId);
        return dishRepository.getAll(restaurantId);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Transactional
    public void delete(@PathVariable int id,
                       @PathVariable int restaurantId) {
        log.info("delete() dish: {}  restaurant: {}", id, restaurantId);
        Dish dish = dishRepository.checkExistsAndGet(id, restaurantId);

        if (!dish.getDate().equals(LocalDate.now())) {
            throw new IllegalRequestException("Can't delete dishes from past");
        }
        dishRepository.deleteIfExist(id);
    }
}