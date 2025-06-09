package org.trading.system.trading.model;

import lombok.*;

import java.math.BigDecimal;

import static org.trading.system.common.util.IdGenerationUtil.generateId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {

    @Builder.Default
    private String walletId = generateId();

    private String userId;

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
}
