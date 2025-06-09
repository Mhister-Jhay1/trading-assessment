package org.trading.system.portfolioManagement.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.trading.system.common.util.IdGenerationUtil.generateId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Portfolio {

    @Builder.Default
    private String portfolioId = generateId();

    private String userId;

    @Builder.Default
    private Map<String, Asset> assets = new ConcurrentHashMap<>();

    @Builder.Default
    private BigDecimal value = BigDecimal.ZERO;
}
