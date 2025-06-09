package org.trading.system.portfolioManagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.dto.request.AssetRequest;
import org.trading.system.portfolioManagement.dto.request.PortfolioRequest;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.repository.PortfolioRepository;
import org.trading.system.portfolioManagement.service.AssetService;
import org.trading.system.portfolioManagement.service.PortfolioService;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserManagementService userManagementService;
    private final AssetService assetService;

    @Override
    public ApiResponse<Portfolio> create(PortfolioRequest request) {
        log.info("Creating portfolio for user: {}", request.getUserId());

        User user = getUserOrReturnError(request.getUserId());
        if (user == null) return ApiResponse.error(HttpStatus.NOT_FOUND.value(), List.of("User not found with ID: " + request.getUserId()));

        Optional<Portfolio> existing = portfolioRepository.findByUserId(user.getUserId());
        if (existing.isPresent()) {
            log.info("Portfolio already exists for user: {}", user.getUserId());
            return ApiResponse.error(HttpStatus.CONFLICT.value(), List.of("Portfolio already exists for the user"));
        }

        Portfolio portfolio = Portfolio.builder().userId(user.getUserId()).build();
        portfolio = portfolioRepository.save(portfolio);

        log.info("Portfolio created successfully for user: {}", user.getUserId());
        return ApiResponse.ok(portfolio);
    }

    @Override
    public ApiResponse<Portfolio> addAsset(AssetRequest request) {
        log.info("Adding asset: {} for user ID: {}", request.getAssetId(), request.getUserId());

        Portfolio portfolio = getPortfolioOrReturnError(request.getUserId());
        if (portfolio == null)
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), List.of("Portfolio not found for user: " + request.getUserId()));

        ApiResponse<Asset> assetResponse = assetService.add(request);
        if (assetResponse.getCode() != HttpStatus.OK.value()) {
            log.error("Failed to add asset: {}. Errors: {}", request.getAssetId(), assetResponse.getErrors());
            return ApiResponse.error(assetResponse.getCode(), assetResponse.getErrors());
        }

        Asset asset = assetResponse.getData();
        portfolio.getAssets().put(asset.getAssetId(), asset);
        BigDecimal totalValue = asset.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        portfolio.setValue(portfolio.getValue().add(totalValue));
        portfolio = portfolioRepository.save(portfolio);

        log.info("Asset added successfully to portfolio for user: {}", request.getUserId());
        return ApiResponse.ok(portfolio);
    }

    @Override
    public ApiResponse<Portfolio> removeAsset(AssetRequest request) {
        log.info("Removing asset: {} for user ID: {}", request.getAssetId(), request.getUserId());

        Portfolio portfolio = getPortfolioOrReturnError(request.getUserId());
        if (portfolio == null)
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), List.of("Portfolio not found for user: " + request.getUserId()));

        if (!portfolio.getAssets().containsKey(request.getAssetId())) {
            log.error("Asset not found in portfolio: {}", request.getAssetId());
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), List.of("Asset not found in portfolio"));
        }

        ApiResponse<Asset> assetResponse = assetService.remove(request);
        if (assetResponse.getCode() != HttpStatus.OK.value()) {
            log.error("Failed to remove asset: {}. Errors: {}", request.getAssetId(), assetResponse.getErrors());
            return ApiResponse.error(assetResponse.getCode(), assetResponse.getErrors());
        }

        Asset asset = assetResponse.getData();
        log.info("Asset Quantity updated in portfolio for user: {}", asset.getQuantity());
        if (asset.getQuantity() == 0) {
            portfolio.getAssets().remove(asset.getAssetId());
        }

        BigDecimal valueReduction = asset.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        portfolio.setValue(portfolio.getValue().subtract(valueReduction));
        portfolio.getAssets().put(asset.getAssetId(), asset);
        portfolio = portfolioRepository.save(portfolio);

        log.info("Asset removed successfully from portfolio for user: {}", request.getUserId());
        return ApiResponse.ok(portfolio);
    }

    private User getUserOrReturnError(String userId) {
        try {
            return userManagementService.getUserById(userId).getData();
        } catch (Exception e) {
            log.error("Failed to fetch user by ID: {}", userId, e);
            return null;
        }
    }

    private Portfolio getPortfolioOrReturnError(String userId) {
        User user = getUserOrReturnError(userId);
        if (user == null) return null;

        Optional<Portfolio> optionalPortfolio = portfolioRepository.findByUserId(user.getUserId());
        return optionalPortfolio.orElse(null);
    }

    @Override
    public Portfolio getPortfolioByUserId(String userId){
        Optional<Portfolio> optionalPortfolio = portfolioRepository.findByUserId(userId);
        return optionalPortfolio.orElse(null);
    }
}
