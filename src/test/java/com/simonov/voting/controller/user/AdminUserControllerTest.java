package com.simonov.voting.controller.user;

import com.simonov.voting.controller.AbstractControllerTest;
import com.simonov.voting.controller.GlobalExceptionHandler;
import com.simonov.voting.model.Role;
import com.simonov.voting.model.User;
import com.simonov.voting.service.UserService;
import com.simonov.voting.to.UserTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import util.JsonUtil;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static com.simonov.voting.controller.user.TestDataUser.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminUserControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminController.REST_URL + '/';

    @Autowired
    private UserService userService;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void create() throws Exception {
        User expectedUser = new User(null, "alfa", "a@a.a", "unique-pass",
                false, new Date(), Collections.singletonList(Role.USER));
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeAndAddAdditionProperty(expectedUser, "password", "unique-pass")))
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
    @WithUserDetails(value = ADMIN_MAIL)
    void createWrong() throws Exception {
        User wrongUser = new User(null, null, "", null, Role.USER, Role.ADMIN);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeAndAddAdditionProperty(wrongUser, "password", "betabeta")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicated() throws Exception {
        User expected = new User(null, "New", USER_MAIL, "newPass", Role.USER, Role.ADMIN);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeAndAddAdditionProperty(expected, "password", "newPass")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.DUPLICATE_EMAIL)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByEmail() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by-email?email=" + admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void notFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + WRONG_USER_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void forbiddenGet() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)).andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void enableInvalid() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + WRONG_USER_ID)
                .param("enabled", "false")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getUnAuthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin, user));
    }


    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        UserTo updated = new UserTo(USER_ID, "UpdatedName", USER_MAIL, "unique-pass1");

        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());


        final Optional<User> opUserFromDb = userService.get(USER_ID);
        assertTrue(opUserFromDb.isPresent(), "User must present in DB");

        USER_MATCHER.assertMatch(opUserFromDb.get(), toMapper.createUserEntity(updated));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID)
                .param("enabled", "false")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        final Optional<User> opUserFromDb = userService.get(USER_ID);
        assertTrue(opUserFromDb.isPresent(), "User must present in DB");
        Assertions.assertFalse(opUserFromDb.get().isEnabled());
    }


    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWrong() throws Exception {
        User wrongUser = User.of(user);
        wrongUser.setName("");
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeAndAddAdditionProperty(wrongUser, "password", "betabeta")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicated() throws Exception {
        User updated = User.of(user);
        updated.setEmail(ADMIN_MAIL);
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(APPLICATION_JSON)
                .content(JsonUtil.writeAndAddAdditionProperty(updated, "password", "betabeta")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.DUPLICATE_EMAIL)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(userService.get(USER_ID).isPresent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteInvalid() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + WRONG_USER_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}