package com.bookanapp.domain.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentPaymentRequest {

    public AppointmentPaymentRequest(long appointmentId, int offset) {
        this.appointmentId = appointmentId;
        this.offset = offset;
    }

    @Positive(message = "INVALID_APPOINTMENT_ID")
    private long appointmentId;
    private int offset;
    private String phoneNumber;
}
