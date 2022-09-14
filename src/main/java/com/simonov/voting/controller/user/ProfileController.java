package com.simonov.voting.controller.user;

import com.simonov.voting.controller.AuthUser;
import com.simonov.voting.model.User;
import com.simonov.voting.service.UserService;
import com.simonov.voting.to.UserTo;
import com.simonov.voting.util.validation.UniqueMailValidator;
import com.simonov.voting.util.validation.ValidationUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = ProfileController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User profile controller")
public class ProfileController {
    static final String REST_URL = "/api/profile";

    private final UniqueMailValidator emailValidator;
    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService,
                             UniqueMailValidator emailValidator) {
        this.userService = userService;
        this.emailValidator = emailValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        log.info("Try register {}", user);
        ValidationUtil.checkNew(user);
        ValidationUtil.checkUserRole(user);
        User created = userService.prepareAndSaveNew(user);
        URI uriNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriNewResource).body(created);
    }

    @GetMapping
    public User get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Try get() -> {}", authUser);
        return authUser.getUser();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        log.info("Try update() -> {}", userTo);
        ValidationUtil.checkAndSetHasId(userTo, authUser.getId());
        userService.updateUser(userTo);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Try delete() -> {}", authUser);
        userService.delete(authUser.getId());
    }


}