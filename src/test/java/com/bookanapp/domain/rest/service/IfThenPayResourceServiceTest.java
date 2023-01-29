package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.*;
import com.bookanapp.domain.repository.ScheduleInvoicingRepository;
import com.bookanapp.domain.rest.dto.AppointmentPaymentRequest;
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

    @Inject
    ScheduleInvoicingRepository scheduleInvoicingRepository;

    Appointment appointment;

    Appointment fixedAppointment;
    Schedule schedule;

    Schedule fixedSchedule;
    final LocalDateTime dateTime = LocalDateTime.now().plusHours(25).truncatedTo(ChronoUnit.MINUTES);

    @BeforeEach
    void setUp() {

        //create schedule

        schedule = new Schedule();
        schedule.setName("Schedule");
        schedule.setScheduleCategory("Category");
        schedule.setAccessibleOnWidget(true);
        schedule.setProviderId(31L);
        schedule.setNoDuration(true);

        var scheduleService = ScheduleServices.builder()
                .description("Service")
                .duration(60)
                .price(90.00F)
                .build();

        schedule.getScheduleServices().add(scheduleService);
        this.scheduleService.saveSchedule(schedule);

        fixedSchedule = new Schedule();
        fixedSchedule.setName("Schedule");
        fixedSchedule.setScheduleCategory("Category");
        fixedSchedule.setAccessibleOnWidget(true);
        fixedSchedule.setProviderId(31L);
        this.scheduleService.saveSchedule(fixedSchedule);


        ScheduleInvoicing invoicing = ScheduleInvoicing.builder()
                .invoice(true)
                .scheduleId(fixedSchedule.getId())
                .price(60F)
                .build();

        this.scheduleInvoicingRepository.save(invoicing);

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

        //create future fixed appointment
        fixedAppointment = new Appointment();
        fixedAppointment.setDateTime(dateTime.plusHours(1));
        fixedAppointment.setProviderId(31);
        fixedAppointment.setBookingName("name");
        fixedAppointment.setScheduleId(fixedSchedule.getId());
        this.appointmentService.saveAppointment(fixedAppointment);


    }

    @Test
    @DisplayName("Should return valid reference when requesting multibanco for appointment on flexible schedule")
    void requestMultibancoReference() {
        var request = new AppointmentPaymentRequest(appointment, 0);
        var response = this.ifThenPayResourceService.requestMultibancoReference(31, request);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Payment);
        var body = (Payment) response.getEntity();
        assertNotNull(body);
        assertEquals(9, body.getMultibancoReference().length());
        assertEquals(90.0F, body.getAmount());
    }

    @Test
    @DisplayName("Should return valid reference when requesting multibanco for appointment on fixed schedule")
    void requestMultibancoReferenceForFixedAppoitnment() {
        var request = new AppointmentPaymentRequest(fixedAppointment, 0);
        var response = this.ifThenPayResourceService.requestMultibancoReference(31, request);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Payment);
        var body = (Payment) response.getEntity();
        assertNotNull(body);
        assertEquals(9, body.getMultibancoReference().length());
        assertEquals(60.0F, body.getAmount());
    }

    @Test
    @DisplayName("Should return 422 when requesting multibanco reference with invalid appointment")
    void requestMultibancoReferenceInvalidAppointment() {
        var request = new AppointmentPaymentRequest(new Appointment(), 0);
        var response = this.ifThenPayResourceService.requestMultibancoReference(31, request);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return 422 when requesting multibanco with invalid provider")
    void requestMultibancoReferenceInvalidProvider() {
        var request = new AppointmentPaymentRequest(appointment, 0);
        var response = this.ifThenPayResourceService.requestMultibancoReference(999, request);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return valid response when requesting mbway payment")
    void requestMbWayPayment() {
        var request = new AppointmentPaymentRequest(fixedAppointment, 0);
        request.setPhoneNumber("+351914749827");
        var response = this.ifThenPayResourceService.requestMbWayPayment(31, request);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Payment);
        var body = (Payment) response.getEntity();
        assertNotNull(body);
        assertTrue(body.getAppointmentId() == fixedAppointment.getId());
    }

    @Test
    @DisplayName("Should return 90 when requesting payment for appointment with services")
    void validatePaymentRequest() {
        var request = new AppointmentPaymentRequest(appointment, 0);
        float amount = (Float) this.ifThenPayResourceService.validatePaymentRequest(31, request, false);
        assertEquals(90F, amount);
    }

    @Test
    @DisplayName("Should return 60 when requesting payment for fixed appointment")
    void validatePaymentRequestFixedAppointment() {
        var request = new AppointmentPaymentRequest(fixedAppointment, 0);
        float amount = (Float) this.ifThenPayResourceService.validatePaymentRequest(31, request, false);
        assertEquals(60F, amount);
    }

    @Test
    @DisplayName("Should return 422 when validating payment request with invalid appointment")
    void validatePaymentRequestInvalidAppointment() {
        var request = new AppointmentPaymentRequest(new Appointment(), 0);
        var response = (Response) this.ifThenPayResourceService.validatePaymentRequest(31, request, false);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return 422 when validating payment request with invalid provider")
    void validatePaymentRequestInvalidProvider() {
        var request = new AppointmentPaymentRequest(appointment, 0);
        var response = (Response)this.ifThenPayResourceService.validatePaymentRequest(999, request, false);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return 422 when validating payment request with invalid phone")
    void validatePaymentRequestInvalidPhone() {
        var request = new AppointmentPaymentRequest(appointment, 0);
        var response = (Response)this.ifThenPayResourceService.validatePaymentRequest(31L, request, true);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }
}
