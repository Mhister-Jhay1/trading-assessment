package org.trading.system.trading.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.trading.dto.request.WalletRequest;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.repository.WalletRepository;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class WalletServiceImplTest {

    @Mock private WalletRepository walletRepository;
    @Mock private UserManagementService userManagementService;

    @InjectMocks private WalletServiceImpl walletService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_successfully_create_wallet_for_existing_user() {
        String userId = "user123";
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        Wallet savedWallet = Wallet.builder().userId(userId).balance(BigDecimal.ZERO).build();

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

        ApiResponse<Wallet> result = walletService.create(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertNotNull(result.getData());
        assertEquals(userId, result.getData().getUserId());
        assertEquals(BigDecimal.ZERO, result.getData().getBalance());
    }

    @Test
    public void test_successfully_top_up_existing_wallet() {
        String userId = "user123";
        double amount = 100.0;
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(amount);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        Wallet existingWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(50.0)).build();
        Wallet updatedWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(150.0)).build();

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(updatedWallet);

        ApiResponse<Wallet> result = walletService.topUp(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertNotNull(result.getData());
        assertEquals(BigDecimal.valueOf(150.0), result.getData().getBalance());
    }

    @Test
    public void test_successfully_retrieve_wallet_by_user_id() {
        String userId = "user123";
        Wallet wallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(100.0)).build();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.valueOf(100.0), result.getBalance());
    }

    @Test
    public void test_return_wallet_with_zero_balance_when_creating() {
        String userId = "user123";
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        Wallet savedWallet = Wallet.builder().userId(userId).balance(BigDecimal.ZERO).build();

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

        ApiResponse<Wallet> result = walletService.create(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertEquals(BigDecimal.ZERO, result.getData().getBalance());
    }

    @Test
    public void test_return_updated_wallet_with_correct_balance() {
        String userId = "user123";
        double amount = 75.5;
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(amount);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        Wallet existingWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(25.5)).build();
        Wallet updatedWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(101.0)).build();

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(updatedWallet);

        ApiResponse<Wallet> result = walletService.topUp(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertEquals(BigDecimal.valueOf(101.0), result.getData().getBalance());
    }

    @Test
    public void test_handle_wallet_creation_when_user_not_exists() {
        String userId = "nonexistent123";
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);

        when(userManagementService.getUserById(userId)).thenThrow(new RuntimeException("User not found"));

        ApiResponse<Wallet> result = walletService.create(request);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getCode());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().contains("User not found with ID: " + userId));
    }

    @Test
    public void test_handle_wallet_creation_when_wallet_already_exists() {
        String userId = "user123";
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        Wallet existingWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(100.0)).build();

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(existingWallet));

        ApiResponse<Wallet> result = walletService.create(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT.value(), result.getCode());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().contains("Wallet already exists for the user"));
    }

    @Test
    public void test_handle_top_up_when_user_not_exists() {
        String userId = "nonexistent123";
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(100.0);

        when(userManagementService.getUserById(userId)).thenThrow(new RuntimeException("User not found"));

        ApiResponse<Wallet> result = walletService.topUp(request);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getCode());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().contains("User not found with ID: " + userId));
    }

    @Test
    public void test_handle_top_up_when_wallet_not_exists() {
        String userId = "user123";
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(100.0);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ApiResponse<Wallet> result = walletService.topUp(request);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getCode());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().contains("Wallet not found for the user"));
    }

    @Test
    public void test_handle_top_up_with_zero_amount() {
        String userId = "user123";
        double amount = 0.0;
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(amount);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        Wallet existingWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(100.0)).build();
        Wallet updatedWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(100.0)).build();

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(updatedWallet);

        ApiResponse<Wallet> result = walletService.topUp(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertEquals(BigDecimal.valueOf(100.0), result.getData().getBalance());
    }

    @Test
    public void test_handle_top_up_with_negative_amount() {
        String userId = "user123";
        double amount = -50.0;
        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(amount);

        User user = User.builder().userId(userId).username("testuser").build();
        ApiResponse<User> userApiResponse = ApiResponse.ok(user);

        Wallet existingWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(100.0)).build();
        Wallet updatedWallet = Wallet.builder().userId(userId).balance(BigDecimal.valueOf(50.0)).build();

        when(userManagementService.getUserById(userId)).thenReturn(userApiResponse);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(updatedWallet);

        ApiResponse<Wallet> result = walletService.topUp(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertEquals(BigDecimal.valueOf(50.0), result.getData().getBalance());
    }
}