package com.bookanapp.domain.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultibancoPaymentRequest {
    private String mbKey;
    private String orderId;
    private float amount;
    private String description;
    private int expiryDays;
}
