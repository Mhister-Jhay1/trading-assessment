package org.trading.system.trading.service.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
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
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TradingServiceImplTest {

    @Mock private WalletRepository walletRepository;
    @Mock private UserManagementService userManagementService;
    @Mock private AssetRepository assetRepository;
    @Mock private PortfolioService portfolioService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private TradingServiceImpl tradingService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_successfully_processes_buy_transaction_with_sufficient_balance() {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransactionType("BUY");

        User user = User.builder()
                .userId("user123")
                .wallet(Wallet.builder().balance(new BigDecimal("1000")).build())
                .portfolio(new Portfolio())
                .build();

        Asset asset = Asset.builder()
                .assetId("asset123")
                .price(new BigDecimal("50"))
                .build();

        ApiResponse<User> userResponse = ApiResponse.ok(user);
        ApiResponse<Portfolio> portfolioResponse = ApiResponse.ok(new Portfolio());

        when(userManagementService.getUserById("user123")).thenReturn(userResponse);
        when(assetRepository.findById("asset123")).thenReturn(Optional.of(asset));
        when(portfolioService.addAsset(any(AssetRequest.class))).thenReturn(portfolioResponse);

        ApiResponse<TradeResponse> result = tradingService.trade(request);

        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertNotNull(result.getData());
        verify(walletRepository).save(any(Wallet.class));
        verify(eventPublisher).publishEvent(any(TradeCompletedEvent.class));
    }

    @Test
    public void test_successfully_processes_sell_transaction_with_sufficient_quantity() {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(5);
        request.setTransactionType("SELL");

        Asset userAsset = Asset.builder()
                .assetId("asset123")
                .quantity(10)
                .price(new BigDecimal("50"))
                .build();

        Map<String, Asset> assets = new HashMap<>();
        assets.put("asset123", userAsset);

        Portfolio portfolio = new Portfolio();
        portfolio.setAssets(assets);

        User user = User.builder()
                .userId("user123")
                .wallet(Wallet.builder().balance(new BigDecimal("500")).build())
                .portfolio(portfolio)
                .build();

        Asset asset = Asset.builder()
                .assetId("asset123")
                .price(new BigDecimal("50"))
                .build();

        ApiResponse<User> userResponse = ApiResponse.ok(user);
        ApiResponse<Portfolio> portfolioResponse = ApiResponse.ok(portfolio);

        when(userManagementService.getUserById("user123")).thenReturn(userResponse);
        when(assetRepository.findById("asset123")).thenReturn(Optional.of(asset));
        when(portfolioService.removeAsset(any(AssetRequest.class))).thenReturn(portfolioResponse);

        ApiResponse<TradeResponse> result = tradingService.trade(request);

        assertEquals(HttpStatus.OK.value(), result.getCode());
        assertNotNull(result.getData());
        verify(walletRepository).save(any(Wallet.class));
        verify(eventPublisher).publishEvent(any(TradeCompletedEvent.class));
    }

    @Test
    public void test_returns_proper_trade_response_with_correct_details() {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransactionType("BUY");

        User user = User.builder()
                .userId("user123")
                .wallet(Wallet.builder().balance(new BigDecimal("1000")).build())
                .portfolio(new Portfolio())
                .build();

        Asset asset = Asset.builder()
                .assetId("asset123")
                .price(new BigDecimal("50"))
                .build();

        ApiResponse<User> userResponse = ApiResponse.ok(user);
        ApiResponse<Portfolio> portfolioResponse = ApiResponse.ok(new Portfolio());

        when(userManagementService.getUserById("user123")).thenReturn(userResponse);
        when(assetRepository.findById("asset123")).thenReturn(Optional.of(asset));
        when(portfolioService.addAsset(any(AssetRequest.class))).thenReturn(portfolioResponse);

        ApiResponse<TradeResponse> result = tradingService.trade(request);

        assertEquals(HttpStatus.OK.value(), result.getCode());
        TradeResponse response = result.getData();
        assertEquals("user123", response.getUserId());
        assertEquals("asset123", response.getAssetId());
        assertEquals(10, response.getQuantity());
        assertEquals(new BigDecimal("500"), response.getTotalPrice());
        assertNotNull(response.getPurchasedAt());
    }

    @Test
    public void test_publishes_trade_completed_event_after_successful_trade() {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransactionType("BUY");

        User user = User.builder()
                .userId("user123")
                .wallet(Wallet.builder().balance(new BigDecimal("1000")).build())
                .portfolio(new Portfolio())
                .build();

        Asset asset = Asset.builder()
                .assetId("asset123")
                .price(new BigDecimal("50"))
                .build();

        ApiResponse<User> userResponse = ApiResponse.ok(user);
        ApiResponse<Portfolio> portfolioResponse = ApiResponse.ok(new Portfolio());

        when(userManagementService.getUserById("user123")).thenReturn(userResponse);
        when(assetRepository.findById("asset123")).thenReturn(Optional.of(asset));
        when(portfolioService.addAsset(any(AssetRequest.class))).thenReturn(portfolioResponse);

        tradingService.trade(request);

        ArgumentCaptor<TradeCompletedEvent> eventCaptor = ArgumentCaptor.forClass(TradeCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        TradeCompletedEvent capturedEvent = eventCaptor.getValue();
        assertEquals("user123", capturedEvent.getUserId());
    }

    @Test
    public void test_updates_wallet_balance_correctly_after_buy_transaction() {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransactionType("BUY");

        Wallet wallet = Wallet.builder().balance(new BigDecimal("1000")).build();
        User user = User.builder()
                .userId("user123")
                .wallet(wallet)
                .portfolio(new Portfolio())
                .build();

        Asset asset = Asset.builder()
                .assetId("asset123")
                .price(new BigDecimal("50"))
                .build();

        ApiResponse<User> userResponse = ApiResponse.ok(user);
        ApiResponse<Portfolio> portfolioResponse = ApiResponse.ok(new Portfolio());

        when(userManagementService.getUserById("user123")).thenReturn(userResponse);
        when(assetRepository.findById("asset123")).thenReturn(Optional.of(asset));
        when(portfolioService.addAsset(any(AssetRequest.class))).thenReturn(portfolioResponse);

        tradingService.trade(request);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());
        Wallet savedWallet = walletCaptor.getValue();
        assertEquals(new BigDecimal("500"), savedWallet.getBalance());
    }

    @Test
    public void test_updates_wallet_balance_correctly_after_sell_transaction() {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(5);
        request.setTransactionType("SELL");

        Asset userAsset = Asset.builder()
                .assetId("asset123")
                .quantity(10)
                .price(new BigDecimal("50"))
                .build();

        Map<String, Asset> assets = new HashMap<>();
        assets.put("asset123", userAsset);

        Portfolio portfolio = new Portfolio();
        portfolio.setAssets(assets);

        Wallet wallet = Wallet.builder().balance(new BigDecimal("500")).build();
        User user = User.builder()
                .userId("user123")
                .wallet(wallet)
                .portfolio(portfolio)
                .build();

        Asset asset = Asset.builder()
                .assetId("asset123")
                .price(new BigDecimal("50"))
                .build();

        ApiResponse<User> userResponse = ApiResponse.ok(user);
        ApiResponse<Portfolio> portfolioResponse = ApiResponse.ok(portfolio);

        when(userManagementService.getUserById("user123")).thenReturn(userResponse);
        when(assetRepository.findById("asset123")).thenReturn(Optional.of(asset));
        when(portfolioService.removeAsset(any(AssetRequest.class))).thenReturn(portfolioResponse);

        tradingService.trade(request);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());
        Wallet savedWallet = walletCaptor.getValue();
        assertEquals(new BigDecimal("750"), savedWallet.getBalance());
    }

    @Test
    public void test_returns_not_found_error_when_user_does_not_exist() {
        TradeRequest request = new TradeRequest();
        request.setUserId("nonexistent");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransactionType("BUY");

        when(userManagementService.getUserById("nonexistent")).thenThrow(new RuntimeException("User not found"));

        ApiResponse<TradeResponse> result = tradingService.trade(request);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getCode());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().contains("User not found with ID: nonexistent"));
    }

    @Test
    public void test_returns_not_found_error_when_asset_does_not_exist() {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("nonexistent");
        request.setQuantity(10);
        request.setTransactionType("BUY");

        User user = User.builder()
                .userId("user123")
                .wallet(Wallet.builder().balance(new BigDecimal("1000")).build())
                .build();

        ApiResponse<User> userResponse = ApiResponse.ok(user);

        when(userManagementService.getUserById("user123")).thenReturn(userResponse);
        when(assetRepository.findById("nonexistent")).thenReturn(Optional.empty());

        ApiResponse<TradeResponse> result = tradingService.trade(request);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getCode());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().contains("Asset not found with ID: nonexistent"));
    }
}