package org.trading.system.trading.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.repository.WalletRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryImpl implements WalletRepository {

    private final Map<String, Wallet> walletStore  = new ConcurrentHashMap<>();

    @Override
    public Wallet save(Wallet wallet) {
        walletStore.put(wallet.getWalletId(), wallet);
        return wallet;
    }

    @Override
    public Optional<Wallet> findById(String walletId) {
        return Optional.ofNullable(
                walletStore.get(walletId));
    }

    @Override
    public Optional<Wallet> findByUserId(String userId) {
        return walletStore.values()
                .stream().filter(portfolio ->
                        portfolio.getUserId().equals(userId))
                .findFirst();
    }
}
