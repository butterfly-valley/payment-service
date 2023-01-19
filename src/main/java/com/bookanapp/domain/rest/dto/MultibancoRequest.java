package com.bookanapp.domain.rest.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultibancoRequest {
    @Positive(message = "INVALID_APPOINTMENT_ID")
    private long appointmentId;
    private int offset;
}
