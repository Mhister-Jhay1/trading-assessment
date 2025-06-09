package org.trading.system.userManagement.model;

import lombok.*;
import org.trading.system.portfolioManagement.model.Portfolio;
import org.trading.system.trading.model.Wallet;

import static org.trading.system.common.util.IdGenerationUtil.generateId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Builder.Default
    private String userId = generateId();

    private String username;

    @Builder.Default
    private long gemsCount = 0;

    @Builder.Default
    private int tradeCount = 0;

    private Portfolio portfolio;

    private Wallet wallet;

}
