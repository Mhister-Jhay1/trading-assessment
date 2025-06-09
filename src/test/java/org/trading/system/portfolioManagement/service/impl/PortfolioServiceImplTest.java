package org.trading.system.portfolioManagement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.dto.request.AssetRequest;
import org.trading.system.portfolioManagement.dto.request.PortfolioRequest;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.repository.PortfolioRepository;
import org.trading.system.portfolioManagement.service.AssetService;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPortfolio_success() {
        PortfolioRequest request = new PortfolioRequest();
        request.setUserId("user123");

        User user = new User();
        user.setUserId("user123");

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.ok(user));
        when(portfolioRepository.findByUserId(eq("user123"))).thenReturn(Optional.empty());
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<Portfolio> response = portfolioService.create(request);

        assertEquals(HttpStatus.OK.value(), response.getCode());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    void createPortfolio_userNotFound() {
        PortfolioRequest request = new PortfolioRequest();
        request.setUserId("user123");

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.error(HttpStatus.NOT_FOUND.value(), List.of("User not found")));

        ApiResponse<Portfolio> response = portfolioService.create(request);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
    }

    @Test
    void addAsset_success() {
        AssetRequest request = new AssetRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);

        Portfolio portfolio = new Portfolio();
        portfolio.setUserId("user123");
        portfolio.setValue(BigDecimal.ZERO);

        Asset asset = new Asset();
        asset.setAssetId("asset123");
        asset.setPrice(BigDecimal.valueOf(100));
        asset.setQuantity(10);
        User user = new User();
        user.setUserId("user123");
        user.setPortfolio(portfolio);

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.ok(user));
        when(portfolioRepository.findByUserId(eq("user123"))).thenReturn(Optional.of(portfolio));
        when(assetService.add(any(AssetRequest.class))).thenReturn(ApiResponse.ok(asset));
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<Portfolio> response = portfolioService.addAsset(request);

        assertEquals(HttpStatus.OK.value(), response.getCode());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    void removeAsset_success() {
        AssetRequest request = new AssetRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(5);

        Portfolio portfolio = new Portfolio();
        portfolio.setUserId("user123");
        portfolio.setValue(BigDecimal.valueOf(500));

        Asset asset = new Asset();
        asset.setAssetId("asset123");
        asset.setPrice(BigDecimal.valueOf(100));
        asset.setQuantity(5);

        portfolio.getAssets().put("asset123", asset);

        User user = new User();
        user.setUserId("user123");
        user.setPortfolio(portfolio);

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.ok(user));
        when(portfolioRepository.findByUserId(eq("user123"))).thenReturn(Optional.of(portfolio));
        when(assetService.remove(any(AssetRequest.class))).thenReturn(ApiResponse.ok(asset));
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<Portfolio> response = portfolioService.removeAsset(request);

        assertEquals(HttpStatus.OK.value(), response.getCode());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    void getPortfolioByUserId_success() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId("user123");

        when(portfolioRepository.findByUserId(eq("user123"))).thenReturn(Optional.of(portfolio));

        Portfolio result = portfolioService.getPortfolioByUserId("user123");

        assertEquals("user123", result.getUserId());
    }

    @Test
    void getPortfolioByUserId_notFound() {
        when(portfolioRepository.findByUserId(eq("user123"))).thenReturn(Optional.empty());

        Portfolio result = portfolioService.getPortfolioByUserId("user123");

        assertNull(result);
    }
}