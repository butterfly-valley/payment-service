package com.bookanapp.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AppointmentId")
    private Long id;
    private String userRemark;
    @NotNull
    private LocalDateTime dateTime;
    @NotNull
    private long providerId;

    private boolean userVisibleMessages=true;
    private boolean providerVisibleMessages=true;
    private String providerBookedUser;
    private String providerBookedUserPhone;
    @Email
    private String providerBookedUserEmail;
    @Transient
    private String displayDate;
    @Transient
    private String welcomeDate;
    private boolean reminderSent;
    private String specialist;
    @Column(name = "scheduleReference")
    private String scheduleCategory;
    @Transient
    private String messageSubject;
    private long duration;
    private String bookingName;
    @Column(name = "AvailabilityId")
    private long scheduleId;
    @Column(name = "serviceAvailabilityId")
    private long serviceScheduleId;
    @Transient
    private boolean past;
    private String restriction;
    private String numberOfSpots;
    private Boolean confirmed=false;
    private String providerRemark;
    @Column(name = "ProvidersUserId")
    private Long customerId;

    @Column(name = "customer_uid")
    private String customerUid;

    private boolean sendSms=false;
    private boolean smsSent=false;
    private String madeBy;
    private boolean missedApp;
    private boolean sendWhatsApp=false;
    @Transient
    private boolean whatsAppSent=false;
    @Transient
    private boolean whatsAppDelivered=false;
    @Transient
    private String displayDuration;
    @Email
    private String anonimousUserEmail;
    private String anonimousUserPhone;
    private boolean providerArchived;
    private boolean userArchived;
    private String phone;
    private Instant booked;
    private String cancelledBy;
    private Instant cancelled;
    private Integer invoiceId;
    private Long facilityId;

    @Embedded
    @ElementCollection()
    @CollectionTable(name = "Appointment_Availability_Type", joinColumns = {
            @JoinColumn(name = "AppointmentId", referencedColumnName = "AppointmentId")
    })
    private List<AppointmentServiceType> appointmentServiceTypes=new ArrayList<>();

}
