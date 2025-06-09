package org.trading.system.portfolioManagement.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trading.system.portfolioManagement.model.Asset;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AssetRepositoryImplTest {

    private AssetRepositoryImpl assetRepository;

    @BeforeEach
    void setUp() throws Exception {
        assetRepository = new AssetRepositoryImpl();
        assetRepository.run(null);
    }

    @Test
    void findById_existingAsset() {
        Optional<Asset> asset = assetRepository.findById("AAPL");
        assertTrue(asset.isPresent());
        assertEquals("Apple Inc.", asset.get().getName());
    }

    @Test
    void findById_nonExistingAsset() {
        Optional<Asset> asset = assetRepository.findById("NON_EXISTENT");
        assertFalse(asset.isPresent());
    }

    @Test
    void findByUserId_noAssets() {
        List<Asset> userAssets = assetRepository.findByUserId("nonexistent_user");
        assertTrue(userAssets.isEmpty());
    }

    @Test
    void findAll() {
        List<Asset> allAssets = assetRepository.findAll();
        assertEquals(10, allAssets.size());
    }

    @Test
    void run_initializesAssets() throws Exception {
        assetRepository = new AssetRepositoryImpl();
        assetRepository.run(null);
        List<Asset> allAssets = assetRepository.findAll();
        assertEquals(10, allAssets.size());
    }
}