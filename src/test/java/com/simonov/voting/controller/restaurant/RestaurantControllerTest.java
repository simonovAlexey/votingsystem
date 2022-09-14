package com.simonov.voting.controller.restaurant;

import com.simonov.voting.controller.AbstractControllerTest;
import com.simonov.voting.controller.user.TestDataUser;
import com.simonov.voting.model.Restaurant;
import com.simonov.voting.to.RestaurantTo;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.simonov.voting.controller.restaurant.TestDataRestaurant.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = TestDataUser.USER_MAIL)
class RestaurantControllerTest extends AbstractControllerTest {

    private static final String REST_URL = RestaurantController.REST_URL + '/';

    @Test
    void getOneWithTodayMenu() throws Exception {
        final Restaurant expected = CH_RESTAUR_1;
        perform(MockMvcRequestBuilders.get(REST_URL + REST_1_ID + "/with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAUR_TO_MATCHER.contentJson(toMapper.createTo(expected)));
    }

    @Test
    void getAllWithTodayMenu() throws Exception {
        final List<RestaurantTo> expectedRestaurantTos = toMapper.getRestaurantTos(List.of(CH_RESTAUR_1, RUM_RESTAUR_2, BLIN_RESTAUR_3));
        perform(MockMvcRequestBuilders.get(REST_URL + "with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAUR_TO_MATCHER.contentJson(expectedRestaurantTos));
    }
}