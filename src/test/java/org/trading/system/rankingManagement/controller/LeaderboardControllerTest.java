package org.trading.system.rankingManagement.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.rankingManagement.dto.response.LeaderBoardResponse;
import org.trading.system.rankingManagement.service.LeaderBoardService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaderBoardService leaderBoardService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void fetchLeaderBoard_success() throws Exception {
        LeaderBoardResponse response1 = new LeaderBoardResponse();
        response1.setRank(1);
        response1.setUsername("Alice");
        response1.setGemsCount(150);

        LeaderBoardResponse response2 = new LeaderBoardResponse();
        response2.setRank(2);
        response2.setUsername("Bob");
        response2.setGemsCount(100);

        ApiResponse<List<LeaderBoardResponse>> apiResponse = ApiResponse.ok(Arrays.asList(response1, response2));

        Mockito.when(leaderBoardService.fetchLeaderBoard(anyInt())).thenReturn(apiResponse);

        mockMvc.perform(get("/api/v1/leaderboard")
                .param("count", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("Alice"))
                .andExpect(jsonPath("$.data[1].username").value("Bob"));
    }

    @Test
    void fetchLeaderBoard_failure() throws Exception {
        ApiResponse<List<LeaderBoardResponse>> apiResponse = ApiResponse.error(500, List.of("Error fetching leaderboard"));

        Mockito.when(leaderBoardService.fetchLeaderBoard(anyInt())).thenReturn(apiResponse);

        mockMvc.perform(get("/api/v1/leaderboard")
                .param("count", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value("Error fetching leaderboard"));
    }
}