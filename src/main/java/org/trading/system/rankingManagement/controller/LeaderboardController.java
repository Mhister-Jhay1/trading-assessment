package org.trading.system.rankingManagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.rankingManagement.dto.response.LeaderBoardResponse;
import org.trading.system.rankingManagement.service.LeaderBoardService;

import java.util.List;

@RestController
@RequestMapping("api/v1/leaderboard")
@RequiredArgsConstructor
@Slf4j
public class LeaderboardController  {

    private final LeaderBoardService leaderBoardService;

    @GetMapping
    public ApiResponse<List<LeaderBoardResponse>> fetchLeaderBoard(@RequestParam("count") int n) {
        return leaderBoardService.fetchLeaderBoard(n);
    }
}
