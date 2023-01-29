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

    public AppointmentPaymentRequest(Appointment appointment, int offset) {
        this.appointment = appointment;
        this.offset = offset;
    }

    private Appointment appointment;
    private int offset;
    private String phoneNumber;
}
