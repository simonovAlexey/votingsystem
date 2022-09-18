package com.simonov.voting.controller.user;

import com.simonov.voting.model.User;
import com.simonov.voting.service.UserService;
import com.simonov.voting.to.UserTo;
import com.simonov.voting.util.validation.UniqueMailValidator;
import com.simonov.voting.util.validation.ValidationUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@Slf4j
@RestController
@Tag(name = "User controller for admin auth")
@RequestMapping(value = AdminController.REST_URL, produces = APPLICATION_JSON_VALUE)
public class AdminController {

    static final String REST_URL = "/api/admin/users";

    private final UserService userService;
    private final UniqueMailValidator emailValidator;

    @Autowired
    public AdminController(UserService userService,
                           UniqueMailValidator emailValidator) {
        this.userService = userService;
        this.emailValidator = emailValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable int id) {
        return ResponseEntity.of(userService.get(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Try getAll()");
        return userService.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createWithLocation(@Valid @RequestBody User user) {
        log.info("Try create() {}", user);
        ValidationUtil.checkNew(user);
        User created = userService.prepareAndSaveNew(user);
        URI uriOfNewResource = fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void update(@Valid @RequestBody UserTo userTo, @PathVariable int id) {
        log.info("Try update() id: {} -> {} ", id, userTo);
        ValidationUtil.checkAndSetHasId(userTo, id);
        userService.updateUser(userTo);
    }

    @GetMapping("/by-email")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        log.info("Try getByEmail {}", email);
        return ResponseEntity.of(userService.findByEmail(email));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        log.info("Try {} user id: {}", enabled ? "enable" : "disable", id);
        userService.enable(id, enabled);
    }
}