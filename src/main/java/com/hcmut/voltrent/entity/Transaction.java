package com.hcmut.voltrent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "commission_fee")
    private double commissionFee;

    @Column(name = "owner_amount")
    private double ownerAmount;

}
