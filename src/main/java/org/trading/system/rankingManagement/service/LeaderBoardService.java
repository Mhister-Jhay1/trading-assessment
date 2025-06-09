package org.trading.system.rankingManagement.service;

import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.rankingManagement.dto.response.LeaderBoardResponse;

import java.util.List;

public interface LeaderBoardService {
    ApiResponse<List<LeaderBoardResponse>> fetchLeaderBoard(int n);
}
