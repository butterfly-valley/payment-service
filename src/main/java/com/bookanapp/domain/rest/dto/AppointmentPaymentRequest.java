package com.bookanapp.domain.rest.dto;

import com.bookanapp.domain.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentPaymentRequest {

    public AppointmentPaymentRequest(Appointment appointment) {
        this.appointment = appointment;
    }

    private Appointment appointment;

    private String phoneNumber;
    private String successUrl;
    private String errorUrl;
    private String cancelUrl;
    private String language;

    private Long providerId;
}
