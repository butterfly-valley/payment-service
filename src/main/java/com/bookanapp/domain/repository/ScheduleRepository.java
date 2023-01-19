package com.bookanapp.domain.repository;

import com.bookanapp.domain.model.Schedule;
import org.springframework.data.repository.CrudRepository;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

    Schedule getByIdAndProviderId(long id, long providerId);

}
