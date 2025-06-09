package org.trading.system.portfolioManagement.repository;

import org.trading.system.portfolioManagement.model.Portfolio;

import java.util.Optional;

public interface PortfolioRepository {

    Portfolio save(Portfolio portfolio);

    Optional<Portfolio> findById(String portfolioId);

    Optional<Portfolio> findByUserId(String userId);
}
