package org.trading.system.userManagement.repository.impl;

import org.springframework.stereotype.Repository;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.repository.UserManagementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserManagementRepositoryImpl implements UserManagementRepository {

    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(
                userStore.get(userId)
        );
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userStore.values()
                .stream().filter(user ->
                        user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public User save(User user) {
        userStore.put(user.getUserId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(
                userStore.values()
        );
    }

    @Override
    public int count() {
        return userStore.size();
    }
}
