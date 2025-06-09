package org.trading.system.trading.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.trading.dto.request.TradeRequest;
import org.trading.system.trading.dto.request.WalletRequest;
import org.trading.system.trading.dto.response.TradeResponse;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.service.TradingService;
import org.trading.system.trading.service.WalletService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradingController.class)
class TradingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    private TradingService tradingService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void topUpWallet() throws Exception {
        WalletRequest request = new WalletRequest();
        request.setUserId("user123");
        request.setAmount(100.0);

        Wallet wallet = new Wallet();
        wallet.setUserId("user123");
        wallet.setBalance(BigDecimal.valueOf(200.0));

        ApiResponse<Wallet> response = ApiResponse.ok(wallet);

        Mockito.when(walletService.topUp(any(WalletRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/trade/wallet/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value("user123"))
                .andExpect(jsonPath("$.data.balance").value(200.0));
    }

    @Test
    void trade() throws Exception {
        TradeRequest request = new TradeRequest();
        request.setUserId("user123");
        request.setAssetId("asset123");
        request.setQuantity(10);
        request.setTransactionType("BUY");

        TradeResponse tradeResponse = new TradeResponse();
        tradeResponse.setUserId("user123");
        tradeResponse.setAssetId("asset123");
        tradeResponse.setQuantity(10);

        ApiResponse<TradeResponse> response = ApiResponse.ok(tradeResponse);

        Mockito.when(tradingService.trade(any(TradeRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value("user123"))
                .andExpect(jsonPath("$.data.assetId").value("asset123"))
                .andExpect(jsonPath("$.data.quantity").value(10));
    }
}