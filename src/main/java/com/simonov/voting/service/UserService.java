package com.simonov.voting.service;

import com.simonov.voting.error.IllegalRequestException;
import com.simonov.voting.model.User;
import com.simonov.voting.repository.UserRepository;
import com.simonov.voting.to.UserTo;
import com.simonov.voting.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private static final Sort SORT_USERS = Sort.by(Sort.Direction.ASC, "name", "email");
    private final UserRepository userRepository;
    private final ToMapper commonMapper;
    public UserService(UserRepository userRepository,
                       ToMapper commonMapper) {
        this.userRepository = userRepository;
        this.commonMapper = commonMapper;
    }

    private static User updateUserEntityFromTo(User user, UserTo userTo) {
        if (userTo == null) {
            return user;
        }
        if (userTo.getName() != null) {
            user.setName(userTo.getName());
        }
        if (userTo.getEmail() != null) {
            user.setEmail(userTo.getEmail().toLowerCase());
        }
        if (userTo.getPassword() != null) {
            user.setPassword(UserUtil.encodePassword(userTo.getPassword()));
        }
        return user;
    }

    public Optional<User> get(int id) {
        log.info("get {}", id);
        return userRepository.findById(id);
    }

    @Transactional
    public void delete(int id) {
        log.info("delete {}", id);
        userRepository.deleteIfExist(id);
    }

    @Transactional
    public User prepareAndSaveNew(User user) {
        return userRepository.save(UserUtil.prepareForSave(user));
    }

    @Transactional
    public User updateUser(UserTo userTo) {
        if (userTo == null) {
            return null;
        }
        final Integer id = userTo.getId();
        final User user = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalRequestException("Not found user id: " + id));
        final User updatedUser = updateUserEntityFromTo(user, userTo);
        userRepository.update(id, user.getName(), updatedUser.getEmail(), updatedUser.isEnabled());
        return updatedUser;
    }

    public List<User> findAll() {
        return userRepository.findAll(SORT_USERS);
    }

    @Transactional
    public void enable(int id, boolean enabled) {
        if (userRepository.existsById(id)) {
            userRepository.enable(id, enabled);
        } else
            throw new IllegalRequestException("Not found user with id= " + id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
