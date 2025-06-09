package org.trading.system.trading.repository;

import org.trading.system.trading.model.Wallet;

import java.util.Optional;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(String walletId);

    Optional<Wallet> findByUserId(String userId);
}
