package org.trading.system.portfolioManagement.event.listener;

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
import org.trading.system.portfolioManagement.dto.request.PortfolioRequest;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.service.PortfolioService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PortfolioEventListenerTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioEventListener portfolioEventListener;

    private static final Logger log = LoggerFactory.getLogger(PortfolioEventListenerTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleUserCreatedEvent_success() {
        UserCreatedEvent event = new UserCreatedEvent(this, "user123");
        Portfolio portfolio = new Portfolio();
        ApiResponse<Portfolio> response = ApiResponse.ok(portfolio);

        when(portfolioService.create(any(PortfolioRequest.class))).thenReturn(response);

        portfolioEventListener.handleUserCreatedEvent(event);

        ArgumentCaptor<PortfolioRequest> portfolioRequestCaptor = ArgumentCaptor.forClass(PortfolioRequest.class);
        verify(portfolioService, times(1)).create(portfolioRequestCaptor.capture());
        assertEquals("user123", portfolioRequestCaptor.getValue().getUserId());

        log.info("Test for successful portfolio creation passed.");
    }

    @Test
    void handleUserCreatedEvent_failure() {
        UserCreatedEvent event = new UserCreatedEvent(this, "user123");
        ApiResponse<Portfolio> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                List.of("Error creating portfolio"));

        when(portfolioService.create(any(PortfolioRequest.class))).thenReturn(response);

        portfolioEventListener.handleUserCreatedEvent(event);

        verify(portfolioService, times(1)).create(any(PortfolioRequest.class));

        log.info("Test for portfolio creation failure passed.");
    }

    @Test
    void handleUserCreatedEvent_exception() {
        UserCreatedEvent event = new UserCreatedEvent(this, "user123");

        doThrow(new RuntimeException("Unexpected error")).when(portfolioService).create(any(PortfolioRequest.class));

        portfolioEventListener.handleUserCreatedEvent(event);

        verify(portfolioService, times(1)).create(any(PortfolioRequest.class));

        log.info("Test for exception during portfolio creation passed.");
    }
}