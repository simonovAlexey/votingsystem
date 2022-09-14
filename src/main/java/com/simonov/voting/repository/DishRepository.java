package com.simonov.voting.repository;

import com.simonov.voting.error.DataConflictException;
import com.simonov.voting.model.Dish;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {

    @Query("SELECT d FROM Dish d WHERE d.id=?1 AND d.restaurant.id=?2")
    Optional<Dish> get(int id, int restaurantId);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=?1 ORDER BY d.date DESC, d.price DESC")
    List<Dish> getAll(int restaurantId);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=?1 AND d.date=?2 ORDER BY d.price DESC")
    List<Dish> getAll(int restaurantId, LocalDate date);

    default Dish checkExistsAndGet(int id, int restaurantId) {
        return get(id, restaurantId).orElseThrow(() ->
                new DataConflictException("Dish id: " + id + " doesn't found or doesn't belong to restaurant id: " + restaurantId));
    }
}