package org.trading.system.userManagement.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.trading.system.common.event.TradeCompletedEvent;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.repository.UserManagementRepository;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserManagementEventListener {

    private final UserManagementRepository userManagementRepository;

    @EventListener(TradeCompletedEvent.class)
    public void handleTradeCompletedEvent(TradeCompletedEvent event) {
        log.info("Handling trade completed event for user with ID: {}", event.getUserId());

        userManagementRepository.findById(event.getUserId()).ifPresentOrElse(
                user -> {
                    updateUserTradeAndGems(user);
                    userManagementRepository.save(user);
                    log.info("User with ID: {} updated successfully", event.getUserId());
                },
                () -> log.error("User not found with ID: {}", event.getUserId())
        );
    }

    private void updateUserTradeAndGems(User user) {
        user.setTradeCount(user.getTradeCount() + 1);
        user.setGemsCount(user.getGemsCount() + 1);

        if (user.getTradeCount() == 5) {
            user.setGemsCount(user.getGemsCount() + 5);
        } else if (user.getTradeCount() == 10) {
            user.setGemsCount(user.getGemsCount() + 10);
        }
    }
}
