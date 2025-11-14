package com.hcmut.voltrent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name = "payments")
public class Payment extends BaseEntity{

    @Id
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    private String gateway;

    private String status;

    private double totalAmount;

    @Column(name = "transaction_ref")
    private String transactionRef;

    private String partnerPayDate;

    private String partnerCode;

    @PrePersist
    public void prePersist() {
        id = System.currentTimeMillis();
    }
}
