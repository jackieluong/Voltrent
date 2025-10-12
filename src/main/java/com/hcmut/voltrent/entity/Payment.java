package com.hcmut.voltrent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "payments")
public class Payment extends BaseEntity{

    @Id
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    private String gateway;

    private String status;

    private Long totalAmount;

    @Column(name = "transaction_ref")
    private String transactionRef;

    @PrePersist
    public void prePersist() {
        id = System.currentTimeMillis();
    }
}
