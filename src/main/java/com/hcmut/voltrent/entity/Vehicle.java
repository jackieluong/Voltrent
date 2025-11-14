package com.hcmut.voltrent.entity;

import java.util.UUID;

import com.hcmut.voltrent.constant.VehicleStatus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_price", columnList = "pricePerHour"),
        @Index(name = "idx_owner", columnList = "owner_id")
})
public class Vehicle extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "owner_email", nullable = false)
    private String ownerEmail;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double pricePerHour;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    private boolean isPaused = false;

    private Double latitude;

    private Double longitude;
}