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
}
