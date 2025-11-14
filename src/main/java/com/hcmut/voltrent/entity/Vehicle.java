package com.hcmut.voltrent.entity;
import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.constant.VehicleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Formula;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "vehicles",
    indexes = {
      @Index(name = "idx_type", columnList = "type"),
      @Index(name = "idx_price", columnList = "pricePerHour"),
      @Index(name = "idx_owner", columnList = "owner_id"),
      @Index(name = "idx_province", columnList = "province"),
      @Index(name = "idx_district", columnList = "district"),
      @Index(name = "idx_ward", columnList = "ward"),
      @Index(name = "idx_address", columnList = "address")
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

  private String brand;

  private String model;

  private String color;

  @Column(unique = true)
  private String licensePlate;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private VehicleType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private VehicleStatus status;

  @Column(nullable = false)
  private Double pricePerHour;

  private String imageUrl;

  @Column(name = "province")
  private String province;

  @Column(name = "district")
  private String district;

  @Column(name = "ward")
  private String ward;

  @Column(name = "address")
  private String address;

  @Formula("CASE WHEN type = 'CAR' THEN 0 WHEN type = 'SCOOTER' THEN 1 ELSE 2 END")
  private int typeSortOrder;
}