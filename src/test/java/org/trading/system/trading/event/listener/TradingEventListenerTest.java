package org.trading.system.trading.event.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.common.event.UserCreatedEvent;
import org.trading.system.trading.dto.request.WalletRequest;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.service.WalletService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TradingEventListenerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TradingEventListener tradingEventListener;

    private static final Logger log = LoggerFactory.getLogger(TradingEventListenerTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleUserCreatedEvent_success() {
        UserCreatedEvent event = new UserCreatedEvent(this, "user123");
        Wallet wallet = new Wallet();
        ApiResponse<Wallet> response = ApiResponse.ok(wallet);

        when(walletService.create(any(WalletRequest.class))).thenReturn(response);

        tradingEventListener.handleUserCreatedEvent(event);

        ArgumentCaptor<WalletRequest> walletRequestCaptor = ArgumentCaptor.forClass(WalletRequest.class);
        verify(walletService, times(1)).create(walletRequestCaptor.capture());
        assertEquals("user123", walletRequestCaptor.getValue().getUserId());

        log.info("Test for successful wallet creation passed.");
    }

    @Test
    void handleUserCreatedEvent_failure() {
        UserCreatedEvent event = new UserCreatedEvent(this, "user123");
        ApiResponse<Wallet> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), List.of("Error creating wallet"));

        when(walletService.create(any(WalletRequest.class))).thenReturn(response);

        tradingEventListener.handleUserCreatedEvent(event);

        verify(walletService, times(1)).create(any(WalletRequest.class));

        log.info("Test for wallet creation failure passed.");
    }

    @Test
    void handleUserCreatedEvent_exception() {
        UserCreatedEvent event = new UserCreatedEvent(this, "user123");

        doThrow(new RuntimeException("Unexpected error")).when(walletService).create(any(WalletRequest.class));

        tradingEventListener.handleUserCreatedEvent(event);

        verify(walletService, times(1)).create(any(WalletRequest.class));

        log.info("Test for exception during wallet creation passed.");
    }
}