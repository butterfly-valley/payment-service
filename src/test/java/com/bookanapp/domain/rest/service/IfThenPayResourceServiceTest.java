package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.*;
import com.bookanapp.domain.repository.PaymentRepository;
import com.bookanapp.domain.repository.ScheduleInvoicingRepository;
import com.bookanapp.domain.rest.dto.AppointmentPaymentRequest;
import com.bookanapp.domain.rest.dto.CCPaymentResponse;
import com.bookanapp.domain.rest.dto.ResponseError;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Instant;
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

    @Inject
    PaymentService paymentService;

    @Inject
    PaymentRepository paymentRepository;

    Appointment appointment;

    Appointment fixedAppointment;
    Schedule schedule;

    Schedule fixedSchedule;
    final LocalDateTime dateTime = LocalDateTime.now().plusHours(25).truncatedTo(ChronoUnit.MINUTES);

    Payment payment;

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

        payment = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.MULTIBANCO)
                .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                .amount(300)
                .created(Instant.now())
                .multibancoReference("ref")
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .orderId("orderId")
                .ccRequestId("ccRequestId")
                .appointmentId(999)
                .providerId(31)
                .build();

        this.paymentService.savePayment(payment);


    }

    @AfterEach
    void tearDown() {
        this.paymentRepository.delete(payment);
    }

    @Test
    @DisplayName("Should return valid reference when requesting multibanco for appointment on flexible schedule")
    void requestMultibancoReference() {
        var request = new AppointmentPaymentRequest(appointment);
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
        var request = new AppointmentPaymentRequest(fixedAppointment);
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
        var appointment = new Appointment();
        appointment.setId(999L);
        var request = new AppointmentPaymentRequest(appointment);
        var response = this.ifThenPayResourceService.requestMultibancoReference(31, request);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return 422 when requesting multibanco with invalid provider")
    void requestMultibancoReferenceInvalidProvider() {
        var request = new AppointmentPaymentRequest(appointment);
        var response = this.ifThenPayResourceService.requestMultibancoReference(999, request);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return valid response when requesting mbway payment")
    void requestMbWayPayment() {
        var request = new AppointmentPaymentRequest(fixedAppointment);
        request.setPhoneNumber("+351914749827");
        var response = this.ifThenPayResourceService.requestMbWayPayment(31, request);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Payment);
        var body = (Payment) response.getEntity();
        assertNotNull(body);
        assertEquals(body.getAppointmentId(), (long) fixedAppointment.getId());
    }

    @Test
    @DisplayName("Should return 90 when requesting payment for appointment with services")
    void validatePaymentRequest() {
        var request = new AppointmentPaymentRequest(appointment);
        float amount = (Float) this.ifThenPayResourceService.validatePaymentRequest(31, request, false);
        assertEquals(90F, amount);
    }

    @Test
    @DisplayName("Should return 60 when requesting payment for fixed appointment")
    void validatePaymentRequestFixedAppointment() {
        var request = new AppointmentPaymentRequest(fixedAppointment);
        float amount = (Float) this.ifThenPayResourceService.validatePaymentRequest(31, request, false);
        assertEquals(60F, amount);
    }

    @Test
    @DisplayName("Should return 422 when validating payment request with invalid appointment")
    void validatePaymentRequestInvalidAppointment() {
        var request = new AppointmentPaymentRequest(new Appointment());
        var response = (Response) this.ifThenPayResourceService.validatePaymentRequest(31, request, false);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return 422 when validating payment request with invalid provider")
    void validatePaymentRequestInvalidProvider() {
        var request = new AppointmentPaymentRequest(appointment);
        var response = (Response)this.ifThenPayResourceService.validatePaymentRequest(999, request, false);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return 422 when validating payment request with invalid phone")
    void validatePaymentRequestInvalidPhone() {
        var request = new AppointmentPaymentRequest(appointment);
        var response = (Response)this.ifThenPayResourceService.validatePaymentRequest(31L, request, true);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Should return payment url when requesting payment with credit card")
    void requestCreditCardPayment() {
        var request = new AppointmentPaymentRequest(fixedAppointment);
        request.setLanguage("pt");
        request.setSuccessUrl("https://bookanapp.com/widget/payments/success");
        request.setErrorUrl("https://bookanapp.com/widget/payments/error");
        request.setCancelUrl("https://bookanapp.com/widget/payments/cancel");
        var response = this.ifThenPayResourceService.requestCreditCardPayment(31, request);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Payment);
        var body = (Payment) response.getEntity();
        assertNotNull(body);
        assertTrue(body.getPaymentUrl().length()>10);
    }

    @Test
    @DisplayName("Should confirm cc payment")
    void confirmCreditCardPayment() {
        var response = this.ifThenPayResourceService.confirmCreditCardPayment(31, "ccRequestId", Payment.PaymentStatus.PAID);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        var savedPayment = this.paymentService.findByRequestId("ccRequestId", 31);
        assertNotNull(savedPayment);
        assertEquals(Payment.PaymentStatus.PAID, savedPayment.getPaymentStatus());
    }

    @Test
    @DisplayName("Should fail cc payment")
    void failCreditCardPayment() {
        var response = this.ifThenPayResourceService.confirmCreditCardPayment(31, "ccRequestId", Payment.PaymentStatus.FAILED);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        var savedPayment = this.paymentService.findByRequestId("ccRequestId", 31);
        assertNotNull(savedPayment);
        assertEquals(Payment.PaymentStatus.FAILED, savedPayment.getPaymentStatus());
    }

    @Test
    @DisplayName("Should return 422 when confirming cc for invalid payment")
    void confirmCreditCardInvalidPayment() {
        var response = this.ifThenPayResourceService.confirmCreditCardPayment(999, "ccRequestId",
                Payment.PaymentStatus.PAID);
        assertNotNull(response);
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatus());
    }
}
