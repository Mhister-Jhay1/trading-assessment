package org.trading.system.userManagement.repository;

import org.trading.system.userManagement.model.User;

import java.util.List;
import java.util.Optional;

public interface UserManagementRepository {

    Optional<User> findById(String userId);

    Optional<User> findByUsername(String username);

    User save(User user);

    List<User> findAll();

    int count();
}
