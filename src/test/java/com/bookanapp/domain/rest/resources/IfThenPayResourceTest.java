package com.bookanapp.domain.rest.resources;

import com.bookanapp.domain.model.Appointment;
import com.bookanapp.domain.rest.service.AppointmentService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@QuarkusTest
@TestHTTPEndpoint(IfThenPayResource.class)
class IfThenPayResourceTest {

    @Inject
    AppointmentService appointmentService;

    Appointment appointment;
    final LocalDateTime dateTime = LocalDateTime.now().plusHours(25).truncatedTo(ChronoUnit.MINUTES);


    @BeforeEach
    void setUp() {

        //create future appointment
        appointment = new Appointment();
        appointment.setDateTime(dateTime.plusHours(1));
        appointment.setProviderId(31);
        appointment.setBookingName("name");
        appointment.setScheduleId(999L);
        this.appointmentService.saveAppointment(appointment);
    }

    @Test
    @DisplayName("Should return multibanco reference")
    void requestMultibancoReference() {

    }
}
