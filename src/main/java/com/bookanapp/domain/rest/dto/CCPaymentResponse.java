package com.bookanapp.domain.rest.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class CCPaymentResponse {

    @JsonProperty("Message")
//    @JsonIgnore
     private String message;
    @JsonProperty("PaymentUrl")
    private String paymentUrl;
    @JsonProperty("RequestId")
//    @JsonIgnore
    private String requestId;
    @JsonProperty("Status")
//    @JsonIgnore
    private String status;
}
