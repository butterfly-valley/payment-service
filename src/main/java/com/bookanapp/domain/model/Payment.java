package com.bookanapp.domain.model;


import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    private float amount;

    @Column(name = "provider_id")
    private long providerId;

    @Column(name = "appointment_id")
    private long appointmentId;

    private Instant created;

    private Instant updated;
    @Column(name = "multibanco_reference")
    private String multibancoReference;

    @Column(name = "multibanco_entity")
    private String multibancoEntity;

    @Column(name = "mbway_phone")
    private String mbwayPhone;
    private String description;
    @Column(name = "payment_provider")
    private PaymentProvider paymentProvider;
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    public enum PaymentProvider{
        IFTHENPAY,
        STRIPE;
    }

    public enum PaymentMethod{
        CREDIT_CARD,
        MULTIBANCO,
        MBWAY
    }

    public enum PaymentStatus{
        PENDING,
        PAID,
        FAILED
    }

}
