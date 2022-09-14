package com.simonov.voting.controller;

import com.simonov.voting.model.User;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.StringJoiner;

@Getter
public class AuthUser extends org.springframework.security.core.userdetails.User {

    private final User user;

    public AuthUser(@NonNull User user) {
        super(user.getEmail(), user.getPassword(), user.getRoles());
        this.user = user;
    }

    public int getId() {
        return user.id();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "AuthUser{", "}")
                .add("" + user)
                .toString();
    }
}