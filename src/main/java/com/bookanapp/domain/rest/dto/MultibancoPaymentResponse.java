package com.bookanapp.domain.rest
        .dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MultibancoPaymentResponse {

    @JsonProperty("Amount")
    private float amount;
    @JsonProperty("Entity")
    private int entity;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("OrderId")
    private String orderId;
    @JsonProperty("Reference")
    private String reference;
    @JsonProperty("RequestId")
    private String requestId;
    @JsonProperty("Status")
    private int status;
    @JsonProperty("ExpiryDate")
    private String expiryDate;

}
