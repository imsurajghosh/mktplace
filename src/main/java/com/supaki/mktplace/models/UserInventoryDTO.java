package com.supaki.mktplace.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInventoryDTO {

    private String inventoryId;

    private UserDTO user;

    private ItemDTO item;

    private SaleInventoryDTO saleInventory;

    private long purchasedAt;

    private boolean isDeleted;
}
