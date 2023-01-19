package com.bookanapp.domain.model;

import lombok.Getter;
import lombok.Setter;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TestAvailability")
@Getter
@Setter
public class Schedule {

    @Id
    @Column(name = "AvailabilityId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "availabilityCategory")
    private String scheduleCategory;
    @JsonbTransient
    private boolean visible;
    @Transient
    @JsonbTransient
    private String viewPaidUntil;
    @JsonbTransient
    private boolean main;

    @Column(name = "ProviderId")
    @JsonbTransient
    private long providerId;


    @Embedded
    @ElementCollection()
    @CollectionTable(name = "Availability_Type", joinColumns = {
            @JoinColumn(name = "AvailabilityId", referencedColumnName = "AvailabilityId")
    })
    private Set<ScheduleServices> scheduleServices = new HashSet();
////    private int version;
    private Boolean mandatoryPhone = false;
    @JsonbTransient
    private boolean live = false;
    @JsonbTransient
    private boolean sendSms = true;
    @JsonbTransient
    private boolean service = false;
    private int minimumNotice;
    private String avatar;
    @JsonbTransient
    @Column(name = "start_date")
    private LocalDate scheduleStart;
    @JsonbTransient
    @Column(name = "end_date")
    private LocalDate scheduleEnd;
    private String restriction;
    private Integer numberOfSpots;
    private Boolean multipleSpots = false;
    private Integer spotsLeft;

    private Boolean noDuration = false;
    private String serviceSchedule;

    @Column(name = "accessible_on_widget")
    @JsonbTransient
    private boolean accessibleOnWidget;

}
