package org.trading.system.portfolioManagement.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.portfolioManagement.model.Asset;
import org.trading.system.portfolioManagement.service.AssetService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssetController.class)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        // Setup code if needed
    }

    @Test
    void getAllAssets_success() throws Exception {
        Asset asset1 = new Asset("AAPL", "Apple Inc.", 100, BigDecimal.valueOf(150.00));
        Asset asset2 = new Asset("GOOGL", "Alphabet Inc.", 50, BigDecimal.valueOf(2800.00));

        List<Asset> assets = Arrays.asList(asset1, asset2);
        ApiResponse<List<Asset>> apiResponse = ApiResponse.ok(assets);

        when(assetService.getAllAssets()).thenReturn(apiResponse);

        mockMvc.perform(get("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].assetId").value("AAPL"))
                .andExpect(jsonPath("$.data[0].name").value("Apple Inc."))
                .andExpect(jsonPath("$.data[1].assetId").value("GOOGL"))
                .andExpect(jsonPath("$.data[1].name").value("Alphabet Inc."));
    }
}