package com.bookanapp.domain.repository;
import com.bookanapp.domain.model.Appointment;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    Appointment findByIdAndProviderIdAndDateTimeIsAfter(long appointmentId, long providerId, LocalDateTime dateTime);
}
