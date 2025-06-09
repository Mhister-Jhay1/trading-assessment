package org.trading.system.portfolioManagement.repository.impl;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Repository;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.repository.AssetRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AssetRepositoryImpl implements AssetRepository, ApplicationRunner {

    private final Map<String, Asset> assetStore = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userAssetStore = new ConcurrentHashMap<>();

    @Override
    public Optional<Asset> findById(String assetId) {
        return Optional.ofNullable(assetStore.get(assetId));
    }

    @Override
    public Asset save(Asset asset, String userId) {
        userAssetStore.computeIfAbsent(userId,
                k -> new ArrayList<>()).add(asset.getAssetId());
        return asset;
    }

   @Override
    public void deleteById(String assetId, String userId) {
        List<String> userAssets = userAssetStore.get(userId);
        if (userAssets != null && userAssets.contains(assetId)) {
            userAssets.remove(assetId);
            if (userAssets.isEmpty()) {
                userAssetStore.remove(userId);
            }
            assetStore.remove(assetId);
        }
    }

    @Override
    public List<Asset> findByUserId(String userId) {
        List<String> assetIds = userAssetStore.get(userId);
        if (assetIds != null) {
            return assetIds.stream()
                    .map(assetStore::get)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public List<Asset> findAll(){
        return new ArrayList<>(
                assetStore.values());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Asset> assets = Arrays.asList(
                new Asset("AAPL", "Apple Inc.", 100, BigDecimal.valueOf(150.00)),
                new Asset("GOOGL", "Alphabet Inc.", 50,  BigDecimal.valueOf(2800.00)),
                new Asset("AMZN", "Amazon.com Inc.", 30,  BigDecimal.valueOf(3400.00)),
                new Asset("MSFT", "Microsoft Corp.", 200,  BigDecimal.valueOf(299.00)),
                new Asset("TSLA", "Tesla Inc.", 150, BigDecimal.valueOf(700.00)),
                new Asset("FB", "Facebook Inc.", 80, BigDecimal.valueOf(350.00)),
                new Asset("NFLX", "Netflix Inc.", 60, BigDecimal.valueOf(590.00)),
                new Asset("NVDA", "NVIDIA Corp.", 120, BigDecimal.valueOf(220.00)),
                new Asset("BABA", "Alibaba Group", 90, BigDecimal.valueOf(160.00)),
                new Asset("V", "Visa Inc.", 110, BigDecimal.valueOf(230.00))
        );
        assetStore.putAll(assets.stream().collect(Collectors.toMap(Asset::getAssetId, asset -> asset)));
    }
}