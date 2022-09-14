package com.simonov.voting.controller.dish;

import com.simonov.voting.controller.AbstractControllerTest;
import com.simonov.voting.controller.GlobalExceptionHandler;
import com.simonov.voting.controller.user.TestDataUser;
import com.simonov.voting.model.Dish;
import com.simonov.voting.repository.DishRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.simonov.voting.controller.dish.TestDataDish.*;
import static com.simonov.voting.controller.restaurant.TestDataRestaurant.REST_1_ID;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.JsonUtil.writeValue;

@WithUserDetails(value = TestDataUser.ADMIN_MAIL)
class DishControllerTest extends AbstractControllerTest {

    static final String REST_URL = DishController.REST_URL + '/';

    @Autowired
    private DishRepository dishRepository;

    @Test
    void getOne() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DISH1_ID, REST_1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(cheb11));
    }

    @Test
    void getAllByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL, REST_1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(
                        List.of(cheb24, cheb23, cheb22, cheb21, cheb14, cheb13, cheb12, cheb11)));
    }

    @Test
    void getAllByRestaurantAndDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "date", REST_1_ID)
                .param("date", NOW.minusDays(1) + ""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(DISH_TO_MATCHER.contentJson(toMapper.createDishTos(chebYesterday)));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DISH13_ID, REST_1_ID))
                .andExpect(status().isNoContent());
        assertFalse(dishRepository.get(DISH1_ID + 12, REST_1_ID).isPresent());
    }

    @Test
    void deleteWrong() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DISH1_ID, REST_1_ID))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create() throws Exception {
        Dish expectedDish = new Dish(null, "NEW-DISH", 5, NOW);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL, REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(writeValue(expectedDish)))
                .andDo(print())
                .andExpect(status().isCreated());

        Dish actualDish = DISH_MATCHER.readFromJson(action);
        int actualDishId = actualDish.id();
        expectedDish.setId(actualDishId);

        DISH_MATCHER.assertMatch(actualDish, expectedDish);

        final Optional<Dish> opDishFromDb = dishRepository.get(actualDishId, REST_1_ID);
        assertTrue(opDishFromDb.isPresent(), "Dish must present in DB");
        DISH_MATCHER.assertMatch(opDishFromDb.get(), expectedDish);
    }

    @Test
    void createWrong() throws Exception {
        Dish invalid = new Dish(null, null, 7);
        perform(MockMvcRequestBuilders.post(REST_URL, REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createDuplicated() throws Exception {
        Dish duplicated = new Dish(null, cheb22.getName(), 5);
        perform(MockMvcRequestBuilders.post(REST_URL, REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(writeValue(duplicated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.DUPLICATE_DISH)));
    }

    @Test
    void update() throws Exception {
        Dish expectedDish = new Dish(DISH1_ID + 12, "UP-DISH", 13, NOW);
        perform(MockMvcRequestBuilders.put(REST_URL + DISH13_ID, REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(writeValue(expectedDish)))
                .andExpect(status().isNoContent());

        final Optional<Dish> opDishFromDb = dishRepository.get(DISH1_ID + 12, REST_1_ID);
        assertTrue(opDishFromDb.isPresent(), "Dish must present in DB");
        DISH_MATCHER.assertMatch(opDishFromDb.get(), expectedDish);
    }

    @Test
    void updateInvalid() throws Exception {
        Dish invalid = new Dish(DISH1_ID + 4, null, 5);
        perform(MockMvcRequestBuilders.put(REST_URL + DISH13_ID, REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicated() throws Exception {
        Dish invalid = new Dish(DISH13_ID, cheb23.getName(), 7);
        perform(MockMvcRequestBuilders.put(REST_URL + DISH13_ID, REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.DUPLICATE_DISH)));
    }
}