package com.bookanapp.domain.rest.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultibancoResponse {
    private int entity;
    private String reference;
    private float amount;
    private String expiryDate;
}
