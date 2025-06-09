package org.trading.system.userManagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
import org.trading.system.userManagement.service.UserManagementService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final UserManagementRepository userManagementRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ApiResponse<User> createUser(CreateUserRequest request) {
        log.info("Attempting to create user with username: {}", request.getUsername());
        try {
            Optional<User> optionalUser = userManagementRepository.findByUsername(request.getUsername());
            if (optionalUser.isPresent()) {
                log.error("User creation failed: User already exists with username: {}", request.getUsername());
                return ApiResponse.error(HttpStatus.CONFLICT.value(),
                        List.of("User already exists with username: " + request.getUsername()));
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .build();

            user = userManagementRepository.save(user);

            eventPublisher.publishEvent(new UserCreatedEvent(this, user.getUserId()));
            log.info("User created successfully with username: {}", request.getUsername());
            return ApiResponse.created(user);
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating user with username: {}", request.getUsername(), e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    List.of("An unexpected error occurred while creating the user."));
        }
    }

    @Override
    public ApiResponse<User> getUserById(String userId) {
        log.info("Attempting to retrieve user with ID: {}", userId);
        try {
            Optional<User> optionalUser = userManagementRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                log.error("User not found with ID: {}", userId);
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                        List.of("User not found with ID: " + userId));
            }

            User user = optionalUser.get();
            enrichUserWithPortfolioAndWallet(user);

            log.info("User retrieved successfully with ID: {}", userId);
            return ApiResponse.ok(user);
        } catch (ApiException e) {
            log.error("ApiException occurred while retrieving user: {}", e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                    List.of(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error occurred while retrieving user with ID: {}", userId, e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    List.of("An unexpected error occurred while retrieving the user."));
        }
    }

    @Override
    public ApiResponse<List<User>> getAllUsers() {
        log.info("Attempting to retrieve all users");
        try {
            List<User> users = userManagementRepository.findAll();

            List<User> processedUsers = users.parallelStream()
                    .peek(this::enrichUserWithPortfolioAndWallet)
                    .collect(Collectors.toList());

            return ApiResponse.ok(processedUsers);
        } catch (Exception e) {
            log.error("Unexpected error occurred while retrieving all users", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    List.of("An unexpected error occurred while retrieving all users."));
        }
    }

    private void enrichUserWithPortfolioAndWallet(User user) {
        try {
            CompletableFuture<Portfolio> portfolioFuture = CompletableFuture.supplyAsync(() -> getPortfolioByUserId(user.getUserId()));
            CompletableFuture<Wallet> walletFuture = CompletableFuture.supplyAsync(() -> getWalletByUserId(user.getUserId()));

            CompletableFuture.allOf(portfolioFuture, walletFuture).join();

            user.setPortfolio(portfolioFuture.get());
            user.setWallet(walletFuture.get());
        } catch (Exception e) {
            log.error("Error occurred while processing user with ID: {}", user.getUserId(), e);
        }
    }

    private Portfolio getPortfolioByUserId(String userId) {
        return portfolioRepository.findByUserId(userId).orElse(null);
    }

    private Wallet getWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId).orElse(null);
    }
}
