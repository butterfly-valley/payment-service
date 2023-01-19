package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Schedule;
import com.bookanapp.domain.model.ScheduleInvoicing;
import com.bookanapp.domain.repository.ScheduleInvoicingRepository;
import com.bookanapp.domain.repository.ScheduleRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
@ApplicationScoped
public class ScheduleService {


    @Inject
    ScheduleRepository scheduleRepository;

    @Inject
    ScheduleInvoicingRepository scheduleInvoicingRepository;


    public Schedule getScheduleById(long scheduleId, long providerId){
        return this.scheduleRepository.getByIdAndProviderId(scheduleId, providerId);
    }

    public void saveSchedule(Schedule schedule) {
        this.scheduleRepository.save(schedule);
    }

    public ScheduleInvoicing getScheduleInvoicing(long scheduleId){
        return this.scheduleInvoicingRepository.getByScheduleId(scheduleId);
    }



}
