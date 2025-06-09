package org.trading.system.portfolioManagement.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.trading.system.common.constants.TransactionType;

@Getter
@Setter
public class AssetRequest {
    private String assetId;
    private String userId;
    private int quantity;
    private TransactionType transType;
}
