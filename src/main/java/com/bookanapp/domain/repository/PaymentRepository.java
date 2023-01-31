package com.bookanapp.domain.repository;
import com.bookanapp.domain.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Payment findByOrderId(String orderId);
    Payment findByAppointmentId(long appointmentId);
    Payment findByCcRequestIdAndProviderId(String requestId, long providerId);

}
