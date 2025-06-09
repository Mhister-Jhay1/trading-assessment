package org.trading.system.trading.service;

import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.trading.dto.request.TradeRequest;
import org.trading.system.trading.dto.response.TradeResponse;

public interface TradingService {
    ApiResponse<TradeResponse> trade(TradeRequest request);
}
