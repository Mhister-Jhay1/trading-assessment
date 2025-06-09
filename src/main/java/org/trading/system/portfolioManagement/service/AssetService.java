package org.trading.system.portfolioManagement.service;

import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.dto.request.AssetRequest;
import org.trading.system.portfolioManagement.model.Asset;

import java.util.List;

public interface AssetService {
    ApiResponse<Asset> add(AssetRequest request);

    ApiResponse<Asset> remove(AssetRequest request);

    ApiResponse<List<Asset>> getAllAssets();
}
