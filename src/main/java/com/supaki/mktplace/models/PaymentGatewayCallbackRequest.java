package com.supaki.mktplace.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentGatewayCallbackRequest {
    private String buyerId;
    private String inventoryId;
    private String paymentStatus;
}
