package com.bookanapp.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleInvoicing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "AvailabilityId")
    private long scheduleId;
    private Float price;
    private String taxId;
    private String taxExemption;
    private Boolean invoice;

    @Column(name = "video_meeting_enabled")
    private boolean videoMeetingEnabled;

}
