package org.trading.system.portfolioManagement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.trading.system.common.constants.TransactionType;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.dto.request.AssetRequest;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.portfolioManagement.repository.AssetRepository;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private AssetServiceImpl assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAsset_success() {
        AssetRequest request = new AssetRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransType(TransactionType.BUY);

        User user = new User();
        user.setUserId("user123");
        user.setPortfolio(new Portfolio());

        Asset asset = new Asset();
        asset.setAssetId("asset123");
        asset.setName("Asset Name");
        asset.setPrice(BigDecimal.valueOf(100));
        asset.setQuantity(10);

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.ok(user));
        when(assetRepository.findById(eq("asset123"))).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class), eq("user123"))).thenReturn(asset);

        ApiResponse<Asset> response = assetService.add(request);

        assertEquals(HttpStatus.OK.value(), response.getCode());
        verify(assetRepository, times(1)).save(any(Asset.class), eq("user123"));
    }

    @Test
    void removeAsset_success() {
        AssetRequest request = new AssetRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(5);
        request.setTransType(TransactionType.SELL);

        User user = new User();
        user.setUserId("user123");
        user.setPortfolio(new Portfolio());

        Asset asset = new Asset();
        asset.setAssetId("asset123");
        asset.setName("Asset Name");
        asset.setPrice(BigDecimal.valueOf(100));
        asset.setQuantity(10);

        user.getPortfolio().getAssets().put("asset123", asset);

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.ok(user));
        when(assetRepository.save(any(Asset.class), eq("user123"))).thenReturn(asset);

        ApiResponse<Asset> response = assetService.remove(request);

        assertEquals(HttpStatus.OK.value(), response.getCode());
        verify(assetRepository, times(1)).save(any(Asset.class), eq("user123"));
    }

    @Test
    void getAllAssets_success() {
        Asset asset1 = new Asset();
        asset1.setAssetId("asset123");
        asset1.setName("Asset Name 1");

        Asset asset2 = new Asset();
        asset2.setAssetId("asset456");
        asset2.setName("Asset Name 2");

        when(assetRepository.findAll()).thenReturn(List.of(asset1, asset2));

        ApiResponse<List<Asset>> response = assetService.getAllAssets();

        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(2, response.getData().size());
    }

    @Test
    void addAsset_userNotFound() {
        AssetRequest request = new AssetRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransType(TransactionType.BUY);

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.error(HttpStatus.NOT_FOUND.value(), List.of("User not found")));

        ApiResponse<Asset> response = assetService.add(request);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
    }

    @Test
    void removeAsset_insufficientQuantity() {
        AssetRequest request = new AssetRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(15);
        request.setTransType(TransactionType.SELL);

        User user = new User();
        user.setUserId("user123");
        user.setPortfolio(new Portfolio());

        Asset asset = new Asset();
        asset.setAssetId("asset123");
        asset.setName("Asset Name");
        asset.setPrice(BigDecimal.valueOf(100));
        asset.setQuantity(10);

        user.getPortfolio().getAssets().put("asset123", asset);

        when(userManagementService.getUserById(eq("user123"))).thenReturn(ApiResponse.ok(user));

        ApiResponse<Asset> response = assetService.remove(request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
    }
}