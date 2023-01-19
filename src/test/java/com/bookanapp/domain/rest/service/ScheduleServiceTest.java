package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Schedule;
import com.bookanapp.domain.model.ScheduleInvoicing;
import com.bookanapp.domain.repository.ScheduleInvoicingRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ScheduleServiceTest {

    @Inject
    ScheduleService scheduleService;

    @Inject
    ScheduleInvoicingRepository scheduleInvoicingRepository;

    Schedule schedule;



    @BeforeEach
    public void init() {

        schedule = new Schedule();
        schedule.setName("Schedule");
        schedule.setScheduleCategory("Category");
        schedule.setAccessibleOnWidget(true);
        schedule.setProviderId(31L);
        this.scheduleService.saveSchedule(schedule);

        ScheduleInvoicing invoicing = ScheduleInvoicing.builder()
                .invoice(true)
                .scheduleId(schedule.getId())
                .price(90F)
                .build();

        this.scheduleInvoicingRepository.save(invoicing);

    }


    @Test
    @DisplayName("Should return valid schedule")
    public void returnSchedule() {
        var savedSchedule = this.scheduleService.getScheduleById(schedule.getId(), 31L);
        assertNotNull(savedSchedule);
        assertEquals(schedule.getId(), savedSchedule.getId());
    }

    @Test
    @DisplayName("Should return null on invalid schedule")
    public void returnNullSchedule() {
        var savedSchedule = this.scheduleService.getScheduleById(999L, 31L);
        assertNull(savedSchedule);
    }

    @Test
    @DisplayName("Should return valid schedule invoicing profile")
    void getScheduleInvoicing() {
        var savedInvoicingProfile = this.scheduleService.getScheduleInvoicing(schedule.getId());
        assertNotNull(savedInvoicingProfile);
        assertEquals(schedule.getId(), savedInvoicingProfile.getScheduleId());
    }
}
