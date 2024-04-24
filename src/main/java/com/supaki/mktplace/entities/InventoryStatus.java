package com.supaki.mktplace.entities;

import lombok.Getter;

public enum InventoryStatus {
    SOLD(0), REMOVED(1), LISTED(2), AWAITING_PAYMENT(3);

    @Getter
    private int order;

    InventoryStatus(int order) {
        this.order = order;
    }
}
