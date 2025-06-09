package org.trading.system.portfolioManagement.repository;

import org.trading.system.portfolioManagement.model.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {

    Optional<Asset> findById(String assetId);

    Asset save(Asset asset, String userId);

    void deleteById(String assetId, String userId);

    List<Asset> findByUserId(String userId);

    List<Asset> findAll();
}
