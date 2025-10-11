package com.hcmut.voltrent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_price", columnList = "pricePerHour")
})
public class Vehicle extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @NotBlank(message = "Vehicle name must not be blank")
    private String name;

    @NotBlank(message = "Vehicle type must not be blank")
    private String type;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be a positive number")
    private Double pricePerHour;

    @NotBlank(message = "Image URL must not be blank")
    private String imageUrl;

    private String status = "available";

    // Getter & Setter cho email vẫn giữ nguyên để bảo toàn logic đã viết
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}