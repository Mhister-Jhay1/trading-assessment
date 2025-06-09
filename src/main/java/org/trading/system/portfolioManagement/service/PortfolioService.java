package org.trading.system.portfolioManagement.service;

import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.dto.request.AssetRequest;
import org.trading.system.portfolioManagement.dto.request.PortfolioRequest;
import org.trading.system.portfolioManagement.model.Portfolio;

public interface PortfolioService {
    ApiResponse<Portfolio> create(PortfolioRequest request);

    ApiResponse<Portfolio> addAsset(AssetRequest request);

    ApiResponse<Portfolio> removeAsset(AssetRequest request);

    Portfolio getPortfolioByUserId(String userId);
}
