package com.simonov.voting.controller.user;

import com.simonov.voting.service.UserService;
import com.simonov.voting.util.validation.UniqueMailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Slf4j
@RequiredArgsConstructor
public class AbstractUserController {

    protected final UserService userService;
    private final UniqueMailValidator emailValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }


}