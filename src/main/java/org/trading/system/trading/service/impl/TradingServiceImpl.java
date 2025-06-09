package org.trading.system.trading.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.trading.system.common.constants.TransactionType;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.common.event.TradeCompletedEvent;
import org.trading.system.portfolioManagement.dto.request.AssetRequest;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.repository.AssetRepository;
import org.trading.system.portfolioManagement.service.PortfolioService;
import org.trading.system.trading.dto.request.TradeRequest;
import org.trading.system.trading.dto.response.TradeResponse;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.repository.WalletRepository;
import org.trading.system.trading.service.TradingService;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingServiceImpl implements TradingService {

    private final WalletRepository walletRepository;
    private final UserManagementService userManagementService;
    private final AssetRepository assetRepository;
    private final PortfolioService portfolioService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ApiResponse<TradeResponse> trade(TradeRequest request) {
        log.info("Received trade request: {}", request);
        try {
            User user = getUserOrReturnError(request.getUserId());
            if (user == null) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                        List.of("User not found with ID: " + request.getUserId()));
            }

            Optional<Asset> optionalAsset = assetRepository.findById(request.getAssetId());
            if (optionalAsset.isEmpty()) {
                log.error("Asset not found with ID: {}", request.getAssetId());
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                        List.of("Asset not found with ID: " + request.getAssetId()));
            }

            Asset asset = optionalAsset.get();
            BigDecimal assetPrice = asset.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

            return switch (request.getTransactionType().toUpperCase()) {
                case "BUY" -> handleBuyTransaction(user, request, assetPrice);
                case "SELL" -> handleSellTransaction(user, request, assetPrice);
                default -> {
                    log.error("Invalid transaction type: {}", request.getTransactionType());
                    yield ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                            List.of("Invalid transaction type: " + request.getTransactionType()));
                }
            };
        } catch (Exception e) {
            log.error("Unexpected error occurred during trade operation", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    List.of("An unexpected error occurred during the trade operation."));
        }
    }

    private ApiResponse<TradeResponse> handleBuyTransaction(User user, TradeRequest request, BigDecimal assetPrice) {
        Wallet wallet = user.getWallet();
        if (wallet.getBalance().compareTo(assetPrice) < 0) {
            log.error("Insufficient balance to trade asset with ID: {}", request.getAssetId());
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                    List.of("Insufficient balance to trade asset with ID: " + request.getAssetId()));
        }

        ApiResponse<Portfolio> portfolioApiResponse = portfolioService.addAsset(createAssetRequest(request, TransactionType.BUY));
        if (portfolioApiResponse.getCode() != HttpStatus.OK.value()) {
            log.error("Failed to add asset to portfolio: {}. Errors: {}", request.getAssetId(), portfolioApiResponse.getErrors());
            return ApiResponse.error(portfolioApiResponse.getCode(), portfolioApiResponse.getErrors());
        }

        wallet.setBalance(wallet.getBalance().subtract(assetPrice));
        walletRepository.save(wallet);

        eventPublisher.publishEvent(new TradeCompletedEvent(this, user.getUserId()));
        return ApiResponse.ok(buildTradeResponse(request, assetPrice));
    }

    private ApiResponse<TradeResponse> handleSellTransaction(User user, TradeRequest request, BigDecimal assetPrice) {
        Map<String, Asset> userAssets = user.getPortfolio().getAssets();
        Asset userAsset = userAssets.get(request.getAssetId());

        if (userAsset == null) {
            log.error("Asset not found in user portfolio: {}", request.getAssetId());
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                    List.of("Asset not found in user portfolio"));
        }

        if (userAsset.getQuantity() < request.getQuantity()) {
            log.error("Insufficient quantity of asset with ID: {} to sell", request.getAssetId());
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                    List.of("Insufficient quantity of asset with ID: " + request.getAssetId() + " to sell"));
        }

        ApiResponse<Portfolio> portfolioApiResponse = portfolioService.removeAsset(createAssetRequest(request, TransactionType.SELL));
        if (portfolioApiResponse.getCode() != HttpStatus.OK.value()) {
            log.error("Failed to remove asset from portfolio: {}. Errors: {}", request.getAssetId(), portfolioApiResponse.getErrors());
            return ApiResponse.error(portfolioApiResponse.getCode(), portfolioApiResponse.getErrors());
        }

        Wallet wallet = user.getWallet();
        wallet.setBalance(wallet.getBalance().add(assetPrice));
        walletRepository.save(wallet);

        eventPublisher.publishEvent(new TradeCompletedEvent(this, user.getUserId()));
        return ApiResponse.ok(buildTradeResponse(request, assetPrice));
    }

    private AssetRequest createAssetRequest(TradeRequest request, TransactionType transactionType) {
        AssetRequest assetRequest = new AssetRequest();
        assetRequest.setUserId(request.getUserId());
        assetRequest.setAssetId(request.getAssetId());
        assetRequest.setQuantity(request.getQuantity());
        assetRequest.setTransType(transactionType);
        return assetRequest;
    }

    private TradeResponse buildTradeResponse(TradeRequest request, BigDecimal totalPrice) {
        TradeResponse response = new TradeResponse();
        response.setUserId(request.getUserId());
        response.setAssetId(request.getAssetId());
        response.setQuantity(request.getQuantity());
        response.setTotalPrice(totalPrice);
        response.setPurchasedAt(LocalDateTime.now());
        return response;
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
