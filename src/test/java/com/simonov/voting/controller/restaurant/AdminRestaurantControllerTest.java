package com.simonov.voting.controller.restaurant;

import com.simonov.voting.controller.AbstractControllerTest;
import com.simonov.voting.controller.GlobalExceptionHandler;
import com.simonov.voting.controller.user.TestDataUser;
import com.simonov.voting.model.Restaurant;
import com.simonov.voting.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import util.JsonUtil;

import java.util.List;
import java.util.Optional;

import static com.simonov.voting.controller.restaurant.TestDataRestaurant.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = TestDataUser.ADMIN_MAIL)
class AdminRestaurantControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestaurantController.REST_URL + '/';

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void create() throws Exception {
        Restaurant expectedRestaurant = new Restaurant(null, "NEW-REST");
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(expectedRestaurant)))
                .andDo(print())
                .andExpect(status().isCreated());

        Restaurant actualRestaurant = RESTAUR_MATCHER.readFromJson(action);
        int actualRestaurantId = actualRestaurant.id();
        expectedRestaurant.setId(actualRestaurantId);
        RESTAUR_MATCHER.assertMatch(actualRestaurant, expectedRestaurant);

        final Optional<Restaurant> opRestFromDb = restaurantRepository.findById(actualRestaurantId);
        assertTrue(opRestFromDb.isPresent(), "Restaurant must present in DB");
        RESTAUR_MATCHER.assertMatch(opRestFromDb.get(), expectedRestaurant);
    }

    @Test
    void createWrong() throws Exception {
        Restaurant wrongRestaurant = new Restaurant(null, null);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(wrongRestaurant)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST_1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(RESTAUR_MATCHER.contentJson(CH_RESTAUR_1));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(RESTAUR_MATCHER.contentJson(
                        List.of(CH_RESTAUR_1, RUM_RESTAUR_2, BLIN_RESTAUR_3)));
    }

    @Test
    void notFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        Restaurant updated = new Restaurant(REST_3_ID, "UPDATED-REST");
        perform(MockMvcRequestBuilders.put(REST_URL + REST_3_ID)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        final Optional<Restaurant> opRestFromDb = restaurantRepository.findById(REST_3_ID);
        assertTrue(opRestFromDb.isPresent(), "Restaurant must present in DB");
        RESTAUR_MATCHER.assertMatch(opRestFromDb.get(), updated);
    }

    @Test
    void updateWrong() throws Exception {
        Restaurant wrongRestaurant = new Restaurant(REST_1_ID, null);
        perform(MockMvcRequestBuilders.put(REST_URL + REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(wrongRestaurant)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + REST_1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteException() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + REST_NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity());
    }




    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicated() throws Exception {
        Restaurant duplicatedRestaurant = new Restaurant(null, CH_RESTAUR_1.getName());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicatedRestaurant)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.DUPLICATE_RESTAURANT)));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicated() throws Exception {
        Restaurant duplicatedRestaurant = new Restaurant(REST_1_ID, RUM_RESTAUR_2.getName());
        perform(MockMvcRequestBuilders.put(REST_URL + REST_1_ID)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicatedRestaurant)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.DUPLICATE_RESTAURANT)));
    }
}