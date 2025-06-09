package org.trading.system.trading.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.trading.dto.request.WalletRequest;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.repository.WalletRepository;
import org.trading.system.trading.service.WalletService;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserManagementService userManagementService;

    @Override
    public ApiResponse<Wallet> create(WalletRequest request) {
        log.info("Creating wallet for user: {}", request.getUserId());

        User user = getUserOrReturnError(request.getUserId());
        if (user == null) return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                List.of("User not found with ID: " + request.getUserId()));

        Optional<Wallet> existing = walletRepository.findByUserId(user.getUserId());
        if (existing.isPresent()) {
            log.info("Wallet already exists for user: {}", user.getUserId());
            return ApiResponse.error(HttpStatus.CONFLICT.value(),
                    List.of("Wallet already exists for the user"));
        }

        Wallet wallet = Wallet.builder().userId(user.getUserId()).build();
        wallet = walletRepository.save(wallet);

        log.info("Wallet created successfully for user: {}", user.getUserId());
        return ApiResponse.ok(wallet);
    }

    @Override
    public ApiResponse<Wallet> topUp(WalletRequest request) {
        log.info("Top up wallet for user: {}", request.getUserId());
        try {
            User user = getUserOrReturnError(request.getUserId());
            if (user == null) {
                log.error("User not found with ID: {}", request.getUserId());
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                        List.of("User not found with ID: " + request.getUserId()));
            }

            Optional<Wallet> optionalWallet = walletRepository.findByUserId(request.getUserId());
            if (optionalWallet.isEmpty()) {
                log.error("Wallet not found for user: {}", request.getUserId());
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                        List.of("Wallet not found for the user"));
            }

            Wallet wallet = optionalWallet.get();
            wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(request.getAmount())));
            wallet = walletRepository.save(wallet);
            log.info("Wallet top up successfully for user: {}", user.getUserId());
            return ApiResponse.ok(wallet);
        } catch (Exception e) {
            log.error("Unexpected error occurred while topping up wallet for user ID: {}", request.getUserId(), e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    List.of("An unexpected error occurred while topping up the wallet."));
        }
    }

    @Override
    public Wallet getWalletByUserId(String userId){
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        return optionalWallet.orElse(null);
    }

    private User getUserOrReturnError(String userId) {
        try {
            return userManagementService.getUserById(userId).getData();
        } catch (Exception e) {
            log.error("Failed to fetch user by ID: {}", userId, e);
            return null;
        }
    }

}
