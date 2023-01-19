package com.bookanapp.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
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
    @JsonIgnore
    private boolean visible;
    @Transient
   @JsonIgnore
    private String viewPaidUntil;
   @JsonIgnore
    private boolean main;

    @Column(name = "ProviderId")
   @JsonIgnore
    private long providerId;


    @Embedded
    @ElementCollection()
    @CollectionTable(name = "Availability_Type", joinColumns = {
            @JoinColumn(name = "AvailabilityId", referencedColumnName = "AvailabilityId")
    })
    private Set<ScheduleServices> scheduleServices = new HashSet();
////    private int version;
    private Boolean mandatoryPhone = false;
   @JsonIgnore
    private boolean live = false;
   @JsonIgnore
    private boolean sendSms = true;
   @JsonIgnore
    private boolean service = false;
    private int minimumNotice;
    private String avatar;
   @JsonIgnore
    @Column(name = "start_date")
    private LocalDate scheduleStart;
   @JsonIgnore
    @Column(name = "end_date")
    private LocalDate scheduleEnd;
    private String restriction;
    private Integer numberOfSpots;
    private Boolean multipleSpots = false;
    private Integer spotsLeft;

    private Boolean noDuration = false;
    private String serviceSchedule;

    @Column(name = "accessible_on_widget")
   @JsonIgnore
    private boolean accessibleOnWidget;

}
