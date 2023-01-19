package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Appointment;
import com.bookanapp.domain.model.AppointmentServiceType;
import com.bookanapp.domain.model.Schedule;
import com.bookanapp.domain.model.ScheduleServices;
import com.bookanapp.domain.rest.dto.MultibancoRequest;
import com.bookanapp.domain.rest.dto.MultibancoResponse;
import com.bookanapp.domain.rest.dto.ResponseError;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class IfThenPayResourceServiceTest {
    @Inject
    AppointmentService appointmentService;

    @Inject
    IfThenPayResourceService ifThenPayResourceService;

    @Inject
    ScheduleService scheduleService;

    Appointment appointment;
    Schedule schedule;
    final LocalDateTime dateTime = LocalDateTime.now().plusHours(25).truncatedTo(ChronoUnit.MINUTES);

    @BeforeEach
    void setUp() {

        //create schedule

        schedule = new Schedule();
        schedule.setName("Schedule");
        schedule.setScheduleCategory("Category");
        schedule.setAccessibleOnWidget(true);
        schedule.setProviderId(31L);

        var scheduleService = ScheduleServices.builder()
                .description("Service")
                .duration(60)
                .price(90.00F)
                .build();

        schedule.getScheduleServices().add(scheduleService);

        this.scheduleService.saveSchedule(schedule);

        //create future service appointment
        appointment = new Appointment();
        appointment.setDateTime(dateTime.plusHours(1));
        appointment.setProviderId(31);
        appointment.setBookingName("name");
        appointment.setScheduleId(schedule.getId());
        AppointmentServiceType serviceType = new AppointmentServiceType();
        serviceType.setDuration(60);
        serviceType.setDescription("Service");
        appointment.getAppointmentServiceTypes().add(serviceType);
        this.appointmentService.saveAppointment(appointment);


    }

    @Test
    @DisplayName("Should return valid reference when requesting multibanco")
    void requestMultibancoReference() {
        var request = new MultibancoRequest(appointment.getId(), 0);
        var response = this.ifThenPayResourceService.requestMultibancoReference(31, request);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof MultibancoResponse);
        var body = (MultibancoResponse) response.getEntity();
        assertTrue(body.getReference().length()==9);
    }

    @Test
    @DisplayName("Should 422 when requesting multibanco with invalid appointment")
    void requestMultibancoReferenceInvalidAppointment() {
        var request = new MultibancoRequest(999, 0);
        var response = this.ifThenPayResourceService.requestMultibancoReference(31, request);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should 422 when requesting multibanco with invalid provider")
    void requestMultibancoReferenceInvalidProvider() {
        var request = new MultibancoRequest(appointment.getId(), 0);
        var response = this.ifThenPayResourceService.requestMultibancoReference(999, request);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }
}
