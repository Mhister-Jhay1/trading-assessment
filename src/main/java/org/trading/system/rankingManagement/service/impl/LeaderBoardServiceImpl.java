package org.trading.system.rankingManagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.rankingManagement.dto.response.LeaderBoardResponse;
import org.trading.system.rankingManagement.service.LeaderBoardService;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private final UserManagementService userManagementService;

    @Override
    public ApiResponse<List<LeaderBoardResponse>> fetchLeaderBoard(int n) {
        log.info("Fetching Leader Board with size: {}", n);

        try {
            ApiResponse<List<User>> listApiResponse = userManagementService.getAllUsers();
            if (listApiResponse.getCode() != HttpStatus.OK.value()) {
                log.error("Failed to fetch users: {}", listApiResponse.getMessage());
                return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        List.of("Failed to fetch users: " + listApiResponse.getMessage()));
            }

            List<User> users = listApiResponse.getData();

            List<LeaderBoardResponse> leaderboard = new ArrayList<>();
            long[] currentRank = {1};
            long[] previousGems = {-1};
            long[] usersWithSameGems = {0};

            users.stream()
                    .sorted(Comparator.comparingLong(User::getGemsCount).reversed())
                    .takeWhile(user -> leaderboard.size() < n)
                    .forEach(user -> {
                        if (user.getGemsCount() != previousGems[0]) {
                            currentRank[0] += usersWithSameGems[0];
                            usersWithSameGems[0] = 1;
                        } else {
                            usersWithSameGems[0]++;
                        }

                        LeaderBoardResponse leaderBoardResponse = new LeaderBoardResponse();
                        leaderBoardResponse.setRank(currentRank[0]);
                        leaderBoardResponse.setUsername(user.getUsername());
                        leaderBoardResponse.setUserId(user.getUserId());
                        leaderBoardResponse.setGemsCount(user.getGemsCount());
                        leaderBoardResponse.setTotalTrade(user.getTradeCount());
                        leaderboard.add(leaderBoardResponse);
                        previousGems[0] = user.getGemsCount();
                    });

            return ApiResponse.ok(leaderboard);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching leaderboard", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    List.of("An unexpected error occurred while fetching the leaderboard."));
        }
    }
}