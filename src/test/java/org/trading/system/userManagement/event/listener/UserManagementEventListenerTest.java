package org.trading.system.userManagement.event.listener;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.trading.system.common.event.TradeCompletedEvent;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.repository.UserManagementRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserManagementEventListenerTest {

    @Mock private UserManagementRepository userManagementRepository;

    @InjectMocks private UserManagementEventListener userManagementEventListener;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_successfully_handles_trade_completed_event_when_user_exists() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(2)
                .gemsCount(5)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        verify(userManagementRepository).findById(userId);
        verify(userManagementRepository).save(user);
        assertEquals(3, user.getTradeCount());
        assertEquals(6, user.getGemsCount());
    }

    @Test
    public void test_increments_trade_count_by_one_on_event_processing() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(3)
                .gemsCount(10)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        assertEquals(4, user.getTradeCount());
    }

    @Test
    public void test_increments_gems_count_by_one_for_regular_trade() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(2)
                .gemsCount(8)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        assertEquals(9, user.getGemsCount());
    }

    @Test
    public void test_saves_updated_user_to_repository_after_processing() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(1)
                .gemsCount(3)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        verify(userManagementRepository).save(user);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userManagementRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(2, savedUser.getTradeCount());
        assertEquals(4, savedUser.getGemsCount());
    }

    @Test
    public void test_generates_info_logs_for_successful_event_handling() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(1)
                .gemsCount(2)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        verify(userManagementRepository).findById(userId);
        verify(userManagementRepository).save(user);
    }

    @Test
    public void test_handles_case_when_user_id_does_not_exist() {
        String userId = "nonexistent123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        when(userManagementRepository.findById(userId)).thenReturn(Optional.empty());

        userManagementEventListener.handleTradeCompletedEvent(event);

        verify(userManagementRepository).findById(userId);
        verify(userManagementRepository, never()).save(any(User.class));
    }

    @Test
    public void test_receives_bonus_five_gems_when_reaching_five_trades() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(4)
                .gemsCount(10)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        assertEquals(5, user.getTradeCount());
        assertEquals(16, user.getGemsCount());
    }

    @Test
    public void test_receives_bonus_ten_gems_when_reaching_ten_trades() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(9)
                .gemsCount(20)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        assertEquals(10, user.getTradeCount());
        assertEquals(31, user.getGemsCount());
    }

    @Test
    public void test_processes_normally_when_trade_count_beyond_milestones() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(15)
                .gemsCount(50)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        assertEquals(16, user.getTradeCount());
        assertEquals(51, user.getGemsCount());
    }

    @Test
    public void test_generates_error_log_when_user_lookup_fails() {
        String userId = "nonexistent123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        when(userManagementRepository.findById(userId)).thenReturn(Optional.empty());

        userManagementEventListener.handleTradeCompletedEvent(event);

        verify(userManagementRepository).findById(userId);
        verify(userManagementRepository, never()).save(any(User.class));
    }

    @Test
    public void test_only_responds_to_trade_completed_event_class() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(1)
                .gemsCount(5)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        userManagementEventListener.handleTradeCompletedEvent(event);

        verify(userManagementRepository).findById(userId);
        verify(userManagementRepository).save(user);
    }

    @Test
    public void test_repository_operations_called_in_correct_sequence() {
        String userId = "user123";
        TradeCompletedEvent event = new TradeCompletedEvent(this, userId);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .tradeCount(2)
                .gemsCount(7)
                .build();

        when(userManagementRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userManagementRepository.save(any(User.class))).thenReturn(user);

        InOrder inOrder = inOrder(userManagementRepository);

        userManagementEventListener.handleTradeCompletedEvent(event);

        inOrder.verify(userManagementRepository).findById(userId);
        inOrder.verify(userManagementRepository).save(user);
    }
}