package com.simonov.voting.controller.user;

import com.simonov.voting.controller.AbstractControllerTest;
import com.simonov.voting.controller.GlobalExceptionHandler;
import com.simonov.voting.model.User;
import com.simonov.voting.service.UserService;
import com.simonov.voting.to.UserTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import util.JsonUtil;

import java.util.Optional;

import static com.simonov.voting.controller.user.ProfileController.REST_URL;
import static com.simonov.voting.controller.user.TestDataUser.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user));
    }

    @Test
    void register() throws Exception {
        User expectedUser = toMapper.createUserEntity(UserTo.of(null, "beta", "b@b.b", "betabeta"));
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeAndAddAdditionProperty(expectedUser, "password", "betabeta")))
                .andDo(print())
                .andExpect(status().isCreated());

        User actualUser = USER_MATCHER.readFromJson(action);
        int actualUserId = actualUser.id();
        expectedUser.setId(actualUserId);
        USER_MATCHER.assertMatch(actualUser, expectedUser);

        final Optional<User> opUserFromDb = userService.get(actualUserId);
        assertTrue(opUserFromDb.isPresent(), "User must present in DB");
        USER_MATCHER.assertMatch(opUserFromDb.get(), expectedUser);
    }

    @Test
    void registerException() throws Exception {
        UserTo newTo = UserTo.of(null, null, null, null);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getUnAuthorize() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithUserDetails(value = USER_MAIL)
    void update() throws Exception {
        UserTo expectedUserTo = UserTo.of(null, "beta", "b@b.b", "betabeta");
        perform(MockMvcRequestBuilders.put(REST_URL).contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(expectedUserTo)))
                .andDo(print())
                .andExpect(status().isNoContent());
        final User actualUser = toMapper.createUserEntity(expectedUserTo)
                .setPassword("betabeta");
        actualUser.setId(USER_ID);


        final Optional<User> opUserFromDb = userService.get(USER_ID);
        assertTrue(opUserFromDb.isPresent(), "User must present in DB");
        USER_MATCHER.assertMatch(opUserFromDb.get(), actualUser);
    }



    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateWrong() throws Exception {
        UserTo expectedUserTo = UserTo.of(null, null, "password", null);
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(expectedUserTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateDuplicated() throws Exception {
        UserTo expectedUserTo = UserTo.of(null, "beta", ADMIN_MAIL, "betabeta");
        perform(MockMvcRequestBuilders.put(REST_URL).contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(expectedUserTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.DUPLICATE_EMAIL)));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andExpect(status().isNoContent());
        USER_MATCHER.assertMatch(userService.findAll(), admin);
    }
}