package org.trading.system.portfolioManagement.model;

import lombok.*;

import java.math.BigDecimal;

import static org.trading.system.common.util.IdGenerationUtil.generateId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {

    @Builder.Default
    private String assetId = generateId();

    private String name;

    private int quantity;

    private BigDecimal price;
}
