package org.trading.system.portfolioManagement.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.repository.PortfolioRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PortfolioRepositoryImpl implements PortfolioRepository {

    private final Map<String, Portfolio> portfolioStore  = new ConcurrentHashMap<>();

    @Override
    public Portfolio save(Portfolio portfolio) {
        portfolioStore.put(portfolio.getPortfolioId(), portfolio);
        return portfolio;
    }

    @Override
    public Optional<Portfolio> findById(String portfolioId) {
        return Optional.ofNullable(
                portfolioStore.get(portfolioId));
    }

    @Override
    public Optional<Portfolio> findByUserId(String userId) {
        return portfolioStore.values()
                .stream().filter(portfolio ->
                        portfolio.getUserId().equals(userId))
                .findFirst();
    }
}
