package org.trading.system.portfolioManagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.trading.system.common.constants.TransactionType;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.dto.request.AssetRequest;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.repository.AssetRepository;
import org.trading.system.portfolioManagement.service.AssetService;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final UserManagementService userManagementService;

    @Override
    public ApiResponse<Asset> add(AssetRequest request) {
        return processAssetTransaction(request);
    }

    @Override
    public ApiResponse<Asset> remove(AssetRequest request) {
        return processAssetTransaction(request);
    }

    @Override
    public ApiResponse<List<Asset>> getAllAssets(){
        return ApiResponse.ok(assetRepository.findAll());
    }

    private ApiResponse<Asset> processAssetTransaction(AssetRequest request) {
        log.info("{} asset with ID: {}", request.getTransType(), request.getAssetId());
        try {
            User user = getUserOrReturnError(request.getUserId());
            if (user == null) return ApiResponse.error(HttpStatus.NOT_FOUND.value(), List.of("User not found with ID: " + request.getUserId()));

            Optional<Asset> optionalUserAsset = getUserAsset(user, request.getAssetId());
            if (request.getTransType() == TransactionType.BUY) {
                return handleBuy(request, optionalUserAsset);
            } else {
                return handleSell(request, optionalUserAsset);
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing asset transaction with ID: {}", request.getAssetId(), e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    List.of("An unexpected error occurred while processing the asset transaction."));
        }
    }

    private Optional<Asset> getUserAsset(User user, String assetId) {
        return Optional.ofNullable(user.getPortfolio().getAssets().get(assetId));
    }

    private ApiResponse<Asset> handleBuy(AssetRequest request, Optional<Asset> optionalUserAsset) {
        Asset userAsset;
        if (optionalUserAsset.isPresent()) {
            userAsset = optionalUserAsset.get();
            userAsset.setQuantity(userAsset.getQuantity() + request.getQuantity());
        } else {
            Optional<Asset> optionalAsset = assetRepository.findById(request.getAssetId());
            if (optionalAsset.isEmpty()) {
                log.error("Asset not found with ID: {}", request.getAssetId());
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                        List.of("Asset not found with ID: " + request.getAssetId()));
            }
            Asset asset = optionalAsset.get();
            userAsset = Asset.builder()
                    .assetId(asset.getAssetId())
                    .name(asset.getName())
                    .price(asset.getPrice())
                    .quantity(request.getQuantity())
                    .build();
        }
        userAsset = assetRepository.save(userAsset, request.getUserId());
        log.info("Asset purchase completed for user ID: {}", request.getUserId());
        return ApiResponse.ok(userAsset);
    }

    private ApiResponse<Asset> handleSell(AssetRequest request, Optional<Asset> optionalUserAsset) {
        if (optionalUserAsset.isEmpty()) {
            log.error("User does not own asset with ID: {}", request.getAssetId());
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                    List.of("User does not own asset with ID: " + request.getAssetId()));
        }

        Asset userAsset = optionalUserAsset.get();
        if (userAsset.getQuantity() < request.getQuantity()) {
            log.error("Insufficient quantity of asset with ID: {} to sell", request.getAssetId());
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                    List.of("Insufficient quantity of asset with ID: " + request.getAssetId() + " to sell"));
        }

        if (userAsset.getQuantity() == request.getQuantity()) {
            userAsset.setQuantity(0);
            assetRepository.deleteById(userAsset.getAssetId(), request.getUserId());
            log.info("Asset fully sold and removed for user ID: {}", request.getUserId());
        } else {
            userAsset.setQuantity(userAsset.getQuantity() - request.getQuantity());
             userAsset = assetRepository.save(userAsset, request.getUserId());
            log.info("Asset quantity updated for user ID: {}", request.getUserId());
        }

        return ApiResponse.ok(userAsset);
    }

    private User getUserOrReturnError(String userId) {
        try {
            return userManagementService.getUserById(userId).getData();
        } catch (Exception e) {
            log.error("Failed to fetch user by ID: {}", userId, e);
            return null;
        }
    }

}