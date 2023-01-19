package com.bookanapp.domain.repository;
import com.bookanapp.domain.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Payment findByOrderId(String orderId);

}
