package org.trading.system.portfolioManagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.service.AssetService;

import java.util.List;

@RestController
@RequestMapping("api/v1/assets")
@RequiredArgsConstructor
@Slf4j
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public ApiResponse<List<Asset>> getAll(){
        return assetService.getAllAssets();
    }
}
