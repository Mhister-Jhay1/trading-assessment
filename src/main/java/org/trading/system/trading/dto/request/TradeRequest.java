package org.trading.system.trading.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;

    @Pattern(regexp = "^(BUY|SELL)$", message = "Transaction type must be either BUY or SELL")
    private String transactionType;
}
