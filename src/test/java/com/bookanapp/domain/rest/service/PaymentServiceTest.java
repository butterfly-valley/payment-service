package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Appointment;
import com.bookanapp.domain.model.Payment;
import com.bookanapp.domain.repository.PaymentRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PaymentServiceTest {

    @Inject
    AppointmentService appointmentService;

    @Inject
    PaymentService paymentService;

    @Inject
    PaymentRepository paymentRepository;


    Appointment appointment;
    final LocalDateTime dateTime = LocalDateTime.now().plusHours(25).truncatedTo(ChronoUnit.MINUTES);

    Payment payment;

    @BeforeEach
    void setUp() {
        //create future appointment
        appointment = new Appointment();
        appointment.setDateTime(dateTime.plusHours(1));
        appointment.setProviderId(31);
        appointment.setBookingName("name");
        appointment.setScheduleId(999L);
        this.appointmentService.saveAppointment(appointment);

        payment = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.MULTIBANCO)
                .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                .amount(300)
                .created(Instant.now())
                .multibancoReference("ref")
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .orderId("orderId")
                .appointmentId(appointment.getId())
                .providerId(31)
                .build();

        this.paymentService.savePayment(payment);

    }

    @AfterEach
    void tearDown() {
        this.paymentRepository.delete(payment);
    }

    @Test
    @DisplayName("Should return payment when it exists in database and when querying by appointment id")
    void findByAppointment() {
        var savedPayment = this.paymentService.findByAppointment(appointment.getId());
        assertEquals(savedPayment.getAppointmentId(), appointment.getId());
        assertEquals(300, payment.getAmount());
    }

    @Test
    @DisplayName("Should return null when payment does not exist and when querying by appointment id")
    void findNullByAppointment() {
        var savedPayment = this.paymentService.findByAppointment(999);
        assertNull(savedPayment);
    }

    @Test
    @DisplayName("Should find payment by orderId")
    void findByOrderId() {
        var savedPayment = this.paymentService.findByOrderId("orderId");
        assertEquals(savedPayment.getAppointmentId(), appointment.getId());
        assertEquals(300, payment.getAmount());
    }

    @Test
    @DisplayName("Should return null when payment does not exist and when querying by order id")
    void findNullByOrderId() {
        var savedPayment = this.paymentService.findByOrderId("999");
        assertNull(savedPayment);
    }

}
