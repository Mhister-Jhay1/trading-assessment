package org.trading.system.trading.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.common.event.UserCreatedEvent;
import org.trading.system.trading.dto.request.WalletRequest;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.service.WalletService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradingEventListener {

    private final WalletService walletService;

    @EventListener(UserCreatedEvent.class)
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Handling user created event for user with ID: {}", event.getUserId());
        String userId = event.getUserId();

        try {
            WalletRequest walletRequest = new WalletRequest();
            walletRequest.setUserId(userId);

            ApiResponse<Wallet> response = walletService.create(walletRequest);
            if (response.getCode() != HttpStatus.OK.value()) {
                log.error("Failed to create wallet for user ID: {}. Errors: {}",
                        userId, response.getErrors());
            } else {
                log.info("Wallet created successfully for user ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred while handling user created event for user ID: {}",
                    userId, e);
        }
    }
}
