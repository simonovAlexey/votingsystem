package com.simonov.voting.repository;

import com.simonov.voting.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User AS u SET u.enabled = :enabled WHERE u.id = :id")
    @Transactional
    void enable(Integer id, boolean enabled);

    @Modifying
    @Query("UPDATE User AS u SET " +
            "u.name = :name, " +
            "u.email = LOWER(:email), " +
            "u.enabled = :enabled " +
            "WHERE u.id = :id")
    @Transactional
    void update(Integer id, String name, String email, boolean enabled);
}