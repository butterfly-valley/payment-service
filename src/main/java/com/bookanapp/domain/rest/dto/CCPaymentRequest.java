package com.bookanapp.domain.rest.dto;

import com.bookanapp.domain.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CCPaymentRequest {

    private String orderId;
    private String amount;
    private String successUrl;
    private String errorUrl;
    private String cancelUrl;
    private String language;

}
