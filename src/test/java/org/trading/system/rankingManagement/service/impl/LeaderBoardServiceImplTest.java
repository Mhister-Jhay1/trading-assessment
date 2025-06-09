package org.trading.system.rankingManagement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.rankingManagement.dto.response.LeaderBoardResponse;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LeaderBoardServiceImplTest {

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private LeaderBoardServiceImpl leaderBoardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchLeaderBoard_success() {
        User user1 = new User();
        user1.setUserId("1");
        user1.setUsername("Alice");
        user1.setGemsCount(100);
        user1.setTradeCount(10);

        User user2 = new User();
        user2.setUserId("2");
        user2.setUsername("Bob");
        user2.setGemsCount(150);
        user2.setTradeCount(15);

        ApiResponse<List<User>> userResponse = ApiResponse.ok(Arrays.asList(user1, user2));

        when(userManagementService.getAllUsers()).thenReturn(userResponse);

        ApiResponse<List<LeaderBoardResponse>> response = leaderBoardService.fetchLeaderBoard(2);

        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(2, response.getData().size());
        assertEquals("Bob", response.getData().get(0).getUsername());
        assertEquals("Alice", response.getData().get(1).getUsername());
    }

    @Test
    void fetchLeaderBoard_failure() {
        ApiResponse<List<User>> userResponse = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), List.of("Error fetching users"));

        when(userManagementService.getAllUsers()).thenReturn(userResponse);

        ApiResponse<List<LeaderBoardResponse>> response = leaderBoardService.fetchLeaderBoard(2);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getCode());
        assertEquals("Failed to fetch users: An error occurred", response.getErrors().get(0));
    }

    @Test
    void fetchLeaderBoard_exception() {
        when(userManagementService.getAllUsers()).thenThrow(new RuntimeException("Unexpected error"));

        ApiResponse<List<LeaderBoardResponse>> response = leaderBoardService.fetchLeaderBoard(2);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getCode());
        assertEquals("An unexpected error occurred while fetching the leaderboard.", response.getErrors().get(0));
    }
}