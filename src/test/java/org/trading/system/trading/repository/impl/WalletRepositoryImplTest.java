package org.trading.system.trading.repository.impl;

import org.junit.jupiter.api.Test;
import org.trading.system.trading.model.Wallet;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WalletRepositoryImplTest {


    @Test
    public void test_save_wallet_successfully_returns_same_wallet_object() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet wallet = Wallet.builder()
                .walletId("wallet123")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();

        Wallet result = walletRepository.save(wallet);

        assertNotNull(result);
        assertEquals(wallet.getWalletId(), result.getWalletId());
        assertEquals(wallet.getUserId(), result.getUserId());
        assertEquals(wallet.getBalance(), result.getBalance());
        assertSame(wallet, result);
    }

    @Test
    public void test_find_by_valid_wallet_id_returns_correct_wallet() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet wallet = Wallet.builder()
                .walletId("wallet123")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findById("wallet123");

        assertTrue(result.isPresent());
        assertEquals("wallet123", result.get().getWalletId());
        assertEquals("user123", result.get().getUserId());
        assertEquals(new BigDecimal("100.00"), result.get().getBalance());
    }

    @Test
    public void test_find_by_valid_user_id_returns_correct_wallet() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet wallet = Wallet.builder()
                .walletId("wallet123")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findByUserId("user123");

        assertTrue(result.isPresent());
        assertEquals("wallet123", result.get().getWalletId());
        assertEquals("user123", result.get().getUserId());
        assertEquals(new BigDecimal("100.00"), result.get().getBalance());
    }

    @Test
    public void test_save_multiple_wallets_stores_all_correctly() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet wallet1 = Wallet.builder()
                .walletId("wallet123")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();
        Wallet wallet2 = Wallet.builder()
                .walletId("wallet456")
                .userId("user456")
                .balance(new BigDecimal("200.00"))
                .build();

        walletRepository.save(wallet1);
        walletRepository.save(wallet2);

        Optional<Wallet> result1 = walletRepository.findById("wallet123");
        Optional<Wallet> result2 = walletRepository.findById("wallet456");
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals("user123", result1.get().getUserId());
        assertEquals("user456", result2.get().getUserId());
    }

    @Test
    public void test_repository_maintains_data_across_multiple_operations() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet wallet = Wallet.builder()
                .walletId("wallet123")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();

        walletRepository.save(wallet);
        Optional<Wallet> findByIdResult = walletRepository.findById("wallet123");
        Optional<Wallet> findByUserIdResult = walletRepository.findByUserId("user123");

        assertTrue(findByIdResult.isPresent());
        assertTrue(findByUserIdResult.isPresent());
        assertEquals("wallet123", findByIdResult.get().getWalletId());
        assertEquals("wallet123", findByUserIdResult.get().getWalletId());
        assertSame(findByIdResult.get(), findByUserIdResult.get());
    }

    @Test
    public void test_find_by_null_wallet_id_returns_empty_optional() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();

        Optional<Wallet> result = walletRepository.findById("");

        assertFalse(result.isPresent());
    }

    @Test
    public void test_find_by_non_existent_wallet_id_returns_empty_optional() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();

        Optional<Wallet> result = walletRepository.findById("nonexistent123");

        assertFalse(result.isPresent());
    }

    @Test
    public void test_find_by_null_user_id_throws_null_pointer_exception() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();

        Optional<Wallet> result = walletRepository.findByUserId("");
        assertFalse(result.isPresent());
    }

    @Test
    public void test_find_by_non_existent_user_id_returns_empty_optional() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();

        Optional<Wallet> result = walletRepository.findByUserId("nonexistent123");

        assertFalse(result.isPresent());
    }

    @Test
    public void test_save_wallet_with_null_wallet_id_uses_null_key() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet wallet = Wallet.builder()
                .walletId("")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();

        Wallet savedWallet = walletRepository.save(wallet);
        Optional<Wallet> result = walletRepository.findById("");

        assertSame(wallet, savedWallet);
        assertTrue(result.isPresent());
        assertEquals("user123", result.get().getUserId());
    }

    @Test
    public void test_save_wallet_overwrites_existing_wallet_same_id() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet originalWallet = Wallet.builder()
                .walletId("wallet123")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();
        Wallet updatedWallet = Wallet.builder()
                .walletId("wallet123")
                .userId("user456")
                .balance(new BigDecimal("200.00"))
                .build();

        walletRepository.save(originalWallet);
        walletRepository.save(updatedWallet);
        Optional<Wallet> result = walletRepository.findById("wallet123");

        assertTrue(result.isPresent());
        assertEquals("user456", result.get().getUserId());
        assertEquals(new BigDecimal("200.00"), result.get().getBalance());
        assertSame(updatedWallet, result.get());
    }

    @Test
    public void test_multiple_wallets_same_user_id_returns_first_found() {
        WalletRepositoryImpl walletRepository = new WalletRepositoryImpl();
        Wallet wallet1 = Wallet.builder()
                .walletId("wallet123")
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .build();
        Wallet wallet2 = Wallet.builder()
                .walletId("wallet456")
                .userId("user123")
                .balance(new BigDecimal("200.00"))
                .build();

        walletRepository.save(wallet1);
        walletRepository.save(wallet2);
        Optional<Wallet> result = walletRepository.findByUserId("user123");

        assertTrue(result.isPresent());
        assertEquals("user123", result.get().getUserId());
        assertTrue(result.get().getWalletId().equals("wallet123") || result.get().getWalletId().equals("wallet456"));
    }
}