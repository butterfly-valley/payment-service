package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Appointment;
import com.bookanapp.domain.repository.AppointmentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class AppointmentService {


    @Inject
    AppointmentRepository appointmentRepository;

    public Appointment getAppointment(long providerId, long appointmentId, int timezoneOffset) {
        var currentDateTime = LocalDateTime.now().plusMinutes(timezoneOffset);
        return this.appointmentRepository.findByIdAndProviderIdAndDateTimeIsAfter(appointmentId, providerId, currentDateTime);
    }

    public void saveAppointment(Appointment appointment) {
        this.appointmentRepository.save(appointment);
    }
}
