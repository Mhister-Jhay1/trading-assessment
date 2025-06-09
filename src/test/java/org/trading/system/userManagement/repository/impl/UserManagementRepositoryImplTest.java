package org.trading.system.userManagement.repository.impl;


import org.junit.jupiter.api.Test;
import org.trading.system.userManagement.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagementRepositoryImplTest {


    // Save a new user and verify it's stored correctly
    @Test
    public void test_save_new_user_stores_correctly() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder()
                .username("testUser")
                .gemsCount(100)
                .tradeCount(5)
                .build();

        // Act
        User savedUser = repository.save(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals(user.getUserId(), savedUser.getUserId());
        assertEquals("testUser", savedUser.getUsername());
        assertEquals(100, savedUser.getGemsCount());
        assertEquals(5, savedUser.getTradeCount());
        Optional<User> retrievedUser = repository.findById(user.getUserId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(user.getUserId(), retrievedUser.get().getUserId());
    }

    // Find user by valid userId returns the correct user
    @Test
    public void test_find_by_valid_user_id_returns_correct_user() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder()
                .username("findByIdUser")
                .gemsCount(200)
                .build();
        repository.save(user);

        // Act
        Optional<User> result = repository.findById(user.getUserId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getUserId(), result.get().getUserId());
        assertEquals("findByIdUser", result.get().getUsername());
        assertEquals(200, result.get().getGemsCount());
    }

    // Find user by valid username returns the correct user
    @Test
    public void test_find_by_valid_username_returns_correct_user() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder()
                .username("uniqueUsername")
                .gemsCount(300)
                .tradeCount(10)
                .build();
        repository.save(user);

        // Act
        Optional<User> result = repository.findByUsername("uniqueUsername");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getUserId(), result.get().getUserId());
        assertEquals("uniqueUsername", result.get().getUsername());
        assertEquals(300, result.get().getGemsCount());
        assertEquals(10, result.get().getTradeCount());
    }

    // Find all users returns complete list of stored users
    @Test
    public void test_find_all_returns_complete_list() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user1 = User.builder().username("user1").gemsCount(100).build();
        User user2 = User.builder().username("user2").gemsCount(200).build();
        User user3 = User.builder().username("user3").gemsCount(300).build();
        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        // Act
        List<User> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("user2")));
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("user3")));
    }

    // Count returns correct number of stored users
    @Test
    public void test_count_returns_correct_number() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        assertEquals(0, repository.count());

        User user1 = User.builder().username("countUser1").build();
        User user2 = User.builder().username("countUser2").build();
        repository.save(user1);
        repository.save(user2);

        // Act
        int count = repository.count();

        // Assert
        assertEquals(2, count);
    }

    // Save multiple users and verify all are stored
    @Test
    public void test_save_multiple_users_all_stored() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user1 = User.builder().username("multiUser1").gemsCount(50).build();
        User user2 = User.builder().username("multiUser2").gemsCount(75).build();
        User user3 = User.builder().username("multiUser3").gemsCount(100).build();

        // Act
        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        // Assert
        assertEquals(3, repository.count());
        assertTrue(repository.findById(user1.getUserId()).isPresent());
        assertTrue(repository.findById(user2.getUserId()).isPresent());
        assertTrue(repository.findById(user3.getUserId()).isPresent());
        assertTrue(repository.findByUsername("multiUser1").isPresent());
        assertTrue(repository.findByUsername("multiUser2").isPresent());
        assertTrue(repository.findByUsername("multiUser3").isPresent());
    }

    // Find user by null userId returns empty Optional
    @Test
    public void test_find_by_null_user_id_returns_empty() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder().username("testUser").build();
        repository.save(user);

        // Act
        Optional<User> result = repository.findById("");

        // Assert
        assertFalse(result.isPresent());
    }

    // Find user by non-existent userId returns empty Optional
    @Test
    public void test_find_by_nonexistent_user_id_returns_empty() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder().username("existingUser").build();
        repository.save(user);

        // Act
        Optional<User> result = repository.findById("nonExistentId");

        // Assert
        assertFalse(result.isPresent());
    }

    // Find user by null username throws NullPointerException
    @Test
    public void test_find_by_null_username_throws_exception() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder().username("testUser").build();
        repository.save(user);

        Optional<User> result =  repository.findByUsername(null);

        assertTrue(result.isEmpty());
    }

    // Find user by non-existent username returns empty Optional
    @Test
    public void test_find_by_nonexistent_username_returns_empty() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder().username("existingUsername").build();
        repository.save(user);

        // Act
        Optional<User> result = repository.findByUsername("nonExistentUsername");

        // Assert
        assertFalse(result.isPresent());
    }

    // Save user with null userId throws exception
    @Test
    public void test_save_user_with_null_user_id_throws_exception() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        User user = User.builder()
                .username("testUser")
                .build();
        user.setUserId(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            repository.save(user);
        });
    }

    // Save user overwrites existing user with same userId
    @Test
    public void test_save_user_overwrites_existing_same_user_id() {
        // Arrange
        UserManagementRepositoryImpl repository = new UserManagementRepositoryImpl();
        String userId = "sameUserId";
        User originalUser = User.builder()
                .username("originalUser")
                .gemsCount(100)
                .build();
        originalUser.setUserId(userId);
        repository.save(originalUser);

        User updatedUser = User.builder()
                .username("updatedUser")
                .gemsCount(200)
                .tradeCount(5)
                .build();
        updatedUser.setUserId(userId);

        // Act
        repository.save(updatedUser);

        // Assert
        assertEquals(1, repository.count());
        Optional<User> result = repository.findById(userId);
        assertTrue(result.isPresent());
        assertEquals("updatedUser", result.get().getUsername());
        assertEquals(200, result.get().getGemsCount());
        assertEquals(5, result.get().getTradeCount());
    }
}