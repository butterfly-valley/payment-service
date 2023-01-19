package com.bookanapp.domain.rest.dto;

import lombok.Data;

@Data
public class MultibancoPaymentResponse {
    private float Amount;
    private String Entity;
    private String Message;
    private String OrderId;
    private String Reference;

    private String RequestId;
    private String Status;

}
