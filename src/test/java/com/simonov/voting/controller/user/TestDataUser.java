package com.simonov.voting.controller.user;

import com.simonov.voting.controller.MatcherFactory;
import com.simonov.voting.model.Role;
import com.simonov.voting.model.User;

public class TestDataUser {
    
    public static final MatcherFactory.Matcher<User> USER_MATCHER = 
            MatcherFactory.usingIgnoringFieldsComparator(User.class, "registered", "password");

    public static final int ADMIN_ID = 1;
    public static final int USER_ID = 2;
    public static final int WRONG_USER_ID = 404;
    public static final String USER_MAIL = "user@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin", Role.ADMIN, Role.USER);
    public static final User user = new User(USER_ID, "User", USER_MAIL, "password", Role.USER);

}
