package org.trading.system.userManagement.service.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.common.event.UserCreatedEvent;
import org.trading.system.common.exception.ApiException;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.repository.PortfolioRepository;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.repository.WalletRepository;
import org.trading.system.userManagement.dto.request.CreateUserRequest;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.repository.UserManagementRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserManagementServiceImplTest {

    @Mock private UserManagementRepository userManagementRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private PortfolioRepository portfolioRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private UserManagementServiceImpl userManagementService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_successfully_creates_new_user_with_valid_username() {

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");

        User savedUser = User.builder()
                .userId("user123")
                .username("testuser")
                .build();

        when(userManagementRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userManagementRepository.save(any(User.class))).thenReturn(savedUser);

        ApiResponse<User> result = userManagementService.createUser(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED.value(), result.getCode());
        assertEquals("testuser", result.getData().getUsername());
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }

    // Successfully retrieves an existing user by ID with enriched portfolio and wallet data
    @Test
    public void test_successfully_retrieves_user_by_id_with_enriched_data() {
        // Arrange
        String userId = "user123";
        User user = User.builder()
                .userId(userId)
                .username("testuser")
                .build();

        Portfolio portfolio = Portfolio.builder()
                .portfolioId("portfolio123")
                .userId(userId)
                .build();

        Wallet wallet = Wallet.builder()
                .walletId("wallet123")
                .userId(userId)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(portfolioRepository.findByUserId(userId)).thenReturn(Optional.of(portfolio));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        // Act
        ApiResponse<User> result = userManagementService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertEquals(userId, result.getData().getUserId());
        assertNotNull(result.getData().getPortfolio());
        assertNotNull(result.getData().getWallet());
    }

    // Successfully retrieves all users with enriched portfolio and wallet data using parallel processing
    @Test
    public void test_successfully_retrieves_all_users_with_enriched_data() {
        // Arrange
        User user1 = User.builder().userId("user1").username("user1").build();
        User user2 = User.builder().userId("user2").username("user2").build();
        List<User> users = List.of(user1, user2);

        Portfolio portfolio1 = Portfolio.builder().portfolioId("portfolio1").userId("user1").build();
        Portfolio portfolio2 = Portfolio.builder().portfolioId("portfolio2").userId("user2").build();

        Wallet wallet1 = Wallet.builder().walletId("wallet1").userId("user1").build();
        Wallet wallet2 = Wallet.builder().walletId("wallet2").userId("user2").build();

        when(userManagementRepository.findAll()).thenReturn(users);
        when(portfolioRepository.findByUserId("user1")).thenReturn(Optional.of(portfolio1));
        when(portfolioRepository.findByUserId("user2")).thenReturn(Optional.of(portfolio2));
        when(walletRepository.findByUserId("user1")).thenReturn(Optional.of(wallet1));
        when(walletRepository.findByUserId("user2")).thenReturn(Optional.of(wallet2));

        // Act
        ApiResponse<List<User>> result = userManagementService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertEquals(2, result.getData().size());
    }

    // Publishes UserCreatedEvent after successful user creation
    @Test
    public void test_publishes_user_created_event_after_successful_creation() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");

        User savedUser = User.builder()
                .userId("user123")
                .username("testuser")
                .build();

        when(userManagementRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userManagementRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        userManagementService.createUser(request);

        // Assert
        ArgumentCaptor<UserCreatedEvent> eventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        UserCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals("user123", capturedEvent.getUserId());
    }

    // Enriches user objects with portfolio and wallet data asynchronously
    @Test
    public void test_enriches_user_with_portfolio_and_wallet_asynchronously() {
        // Arrange
        String userId = "user123";
        User user = User.builder()
                .userId(userId)
                .username("testuser")
                .build();

        Portfolio portfolio = Portfolio.builder()
                .portfolioId("portfolio123")
                .userId(userId)
                .build();

        Wallet wallet = Wallet.builder()
                .walletId("wallet123")
                .userId(userId)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(portfolioRepository.findByUserId(userId)).thenReturn(Optional.of(portfolio));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        // Act
        ApiResponse<User> result = userManagementService.getUserById(userId);

        // Assert
        assertNotNull(result.getData().getPortfolio());
        assertNotNull(result.getData().getWallet());
        assertEquals("portfolio123", result.getData().getPortfolio().getPortfolioId());
        assertEquals("wallet123", result.getData().getWallet().getWalletId());
    }

    // Returns conflict error when attempting to create user with existing username
    @Test
    public void test_returns_conflict_error_for_existing_username() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("existinguser");

        User existingUser = User.builder()
                .userId("user123")
                .username("existinguser")
                .build();

        when(userManagementRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // Act
        ApiResponse<User> result = userManagementService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT.value(), result.getCode());
        assertTrue(result.getErrors().get(0).contains("User already exists with username: existinguser"));
        verify(userManagementRepository, never()).save(any(User.class));
        verify(eventPublisher, never()).publishEvent(any(UserCreatedEvent.class));
    }

    // Returns not found error when retrieving user with non-existent ID
    @Test
    public void test_returns_not_found_error_for_non_existent_user_id() {
        // Arrange
        String userId = "nonexistent123";

        when(userManagementRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> result = userManagementService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getCode());
        assertTrue(result.getErrors().get(0).contains("User not found with ID: nonexistent123"));
        assertNull(result.getData());
    }

    // Handles null or empty portfolio/wallet data gracefully during enrichment
    @Test
    public void test_handles_null_portfolio_wallet_data_gracefully() {
        // Arrange
        String userId = "user123";
        User user = User.builder()
                .userId(userId)
                .username("testuser")
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(portfolioRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> result = userManagementService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertNull(result.getData().getPortfolio());
        assertNull(result.getData().getWallet());
    }

    // Handles CompletableFuture exceptions during async portfolio and wallet retrieval
    @Test
    public void test_handles_completable_future_exceptions_during_async_retrieval() {
        // Arrange
        String userId = "user123";
        User user = User.builder()
                .userId(userId)
                .username("testuser")
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(portfolioRepository.findByUserId(userId)).thenThrow(new RuntimeException("Portfolio service error"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> result = userManagementService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertEquals(userId, result.getData().getUserId());
    }

    // Returns empty list when no users exist in the system
    @Test
    public void test_returns_empty_list_when_no_users_exist() {
        // Arrange
        when(userManagementRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ApiResponse<List<User>> result = userManagementService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertTrue(result.getData().isEmpty());
    }

    // Handles ApiException specifically and returns bad request response with custom message
    @Test
    public void test_handles_api_exception_and_returns_bad_request() {
        // Arrange
        String userId = "user123";
        User user = User.builder()
                .userId(userId)
                .username("testuser")
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(portfolioRepository.findByUserId(userId)).thenThrow(new ApiException("Custom API error"));

        // Act
        ApiResponse<User> result = userManagementService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertNull(result.getData().getPortfolio());
    }

    // Catches and handles generic exceptions returning internal server error responses
    @Test
    public void test_catches_generic_exceptions_returns_internal_server_error() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");

        when(userManagementRepository.findByUsername("testuser")).thenThrow(new RuntimeException("Database connection error"));

        // Act
        ApiResponse<User> result = userManagementService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getCode());
        assertTrue(result.getErrors().get(0).contains("An unexpected error occurred while creating the user."));
        verify(eventPublisher, never()).publishEvent(any(UserCreatedEvent.class));
    }
}