package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Appointment;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
class AppointmentServiceTest {
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
    @DisplayName("Should return appointment")
    void getAppointment() {
        var savedAppointment = this.appointmentService.getAppointment(31l, appointment.getId(), 0);
        assertNotNull(savedAppointment);
        assertEquals(appointment.getId(), savedAppointment.getId());
    }

    @Test
    @DisplayName("Should return null on invalid appointment id")
    void getAppointmentInvalidId() {
        var savedAppointment = this.appointmentService.getAppointment(31l, 999, 0);
        assertNull(savedAppointment);
    }
}
