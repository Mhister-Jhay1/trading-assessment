package org.trading.system.trading.service;

import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.trading.dto.request.WalletRequest;
import org.trading.system.trading.model.Wallet;

public interface WalletService {
    ApiResponse<Wallet> create(WalletRequest request);

    ApiResponse<Wallet> topUp(WalletRequest request);

    Wallet getWalletByUserId(String userId);
}
