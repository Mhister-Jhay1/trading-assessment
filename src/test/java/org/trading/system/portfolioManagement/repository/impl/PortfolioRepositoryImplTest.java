package org.trading.system.portfolioManagement.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trading.system.portfolioManagement.model.Portfolio;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioRepositoryImplTest {

    private PortfolioRepositoryImpl portfolioRepository;

    @BeforeEach
    void setUp() {
        portfolioRepository = new PortfolioRepositoryImpl();
    }

    @Test
    void saveAndFindById() {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId("portfolio123");
        portfolio.setUserId("user123");

        portfolioRepository.save(portfolio);

        Optional<Portfolio> retrievedPortfolio = portfolioRepository.findById("portfolio123");
        assertTrue(retrievedPortfolio.isPresent());
        assertEquals("user123", retrievedPortfolio.get().getUserId());
    }

    @Test
    void findById_notFound() {
        Optional<Portfolio> retrievedPortfolio = portfolioRepository.findById("nonexistent");
        assertFalse(retrievedPortfolio.isPresent());
    }

    @Test
    void findByUserId() {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId("portfolio123");
        portfolio.setUserId("user123");

        portfolioRepository.save(portfolio);

        Optional<Portfolio> retrievedPortfolio = portfolioRepository.findByUserId("user123");
        assertTrue(retrievedPortfolio.isPresent());
        assertEquals("portfolio123", retrievedPortfolio.get().getPortfolioId());
    }

    @Test
    void findByUserId_notFound() {
        Optional<Portfolio> retrievedPortfolio = portfolioRepository.findByUserId("nonexistent");
        assertFalse(retrievedPortfolio.isPresent());
    }
}