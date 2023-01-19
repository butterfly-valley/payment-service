package com.bookanapp.domain.repository;

import com.bookanapp.domain.model.ScheduleInvoicing;
import org.springframework.data.repository.CrudRepository;

public interface ScheduleInvoicingRepository extends CrudRepository<ScheduleInvoicing, Long> {

    ScheduleInvoicing getByScheduleId(long scheduleId);
}
