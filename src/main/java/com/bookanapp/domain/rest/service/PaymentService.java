package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Payment;
import com.bookanapp.domain.repository.PaymentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PaymentService {

    @Inject
    PaymentRepository paymentRepository;

    public void savePayment(Payment payment) {
        this.paymentRepository.save(payment);
    }

    public Payment findByAppointment(long appointmentId) {
        return this.paymentRepository.findByAppointmentId(appointmentId);
    }

    public Payment findByOrderId(String orderId) {
        return this.paymentRepository.findByOrderId(orderId);
    }

    public Payment findByRequestId(String requestId, long providerId) {
        return this.paymentRepository.findByCcRequestIdAndProviderId(requestId, providerId);
    }
}
