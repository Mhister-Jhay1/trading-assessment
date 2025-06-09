package org.trading.system.trading.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.trading.dto.request.TradeRequest;
import org.trading.system.trading.dto.request.WalletRequest;
import org.trading.system.trading.dto.response.TradeResponse;
import org.trading.system.trading.model.Wallet;
import org.trading.system.trading.service.TradingService;
import org.trading.system.trading.service.WalletService;

@RestController
@RequestMapping("api/v1/trade")
@RequiredArgsConstructor
@Slf4j
public class TradingController {

    private final WalletService walletService;
    private final TradingService tradingService;

    @PostMapping("wallet/top-up")
    public ApiResponse<Wallet> topUpWallet(@Valid @RequestBody WalletRequest request){
        return walletService.topUp(request);
    }

    @PostMapping()
    public ApiResponse<TradeResponse> trade(@Valid @RequestBody TradeRequest request){
        return tradingService.trade(request);
    }
}
