package org.trading.system.portfolioManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortfolioRequest {

    @NotBlank(message = "User ID is required")
    private String userId;
}
