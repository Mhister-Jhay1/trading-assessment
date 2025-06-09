package org.trading.system.portfolioManagement.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.common.event.UserCreatedEvent;
import org.trading.system.portfolioManagement.dto.request.PortfolioRequest;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.service.PortfolioService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortfolioEventListener {

    private final PortfolioService portfolioService;

    @EventListener(UserCreatedEvent.class)
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Handling user created event for user with ID: {}", event.getUserId());
        String userId = event.getUserId();

        try {
            PortfolioRequest portfolioRequest = new PortfolioRequest();
            portfolioRequest.setUserId(userId);

            ApiResponse<Portfolio> response = portfolioService.create(portfolioRequest);
            if (response.getCode() != HttpStatus.OK.value()) {
                log.error("Failed to create portfolio for user ID: {}. Errors: {}",
                        userId, response.getErrors());
            } else {
                log.info("Portfolio created successfully for user ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred while handling user created event for user ID: {}",
                    userId, e);
        }
    }
}
