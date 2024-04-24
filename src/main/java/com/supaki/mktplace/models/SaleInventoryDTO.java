package com.supaki.mktplace.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.supaki.mktplace.entities.InventoryStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SaleInventoryDTO {

    private String inventoryId;

    private String ownerId;

    private String itemId;

    private String itemName;

    @Min(value = 1000)
    @Max(value = 100000)
    @NotNull
    private Long listingPrice;

    private long updatedAt;

    private InventoryStatus status;

    private String buyerId;
}
