package com.hcmut.voltrent.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueTransactionResponse {
    private Long transactionId;
    private Long bookingId;
    private String vehicleName;
    private String customerName;
    private LocalDateTime completedAt;
    private double totalAmount;
    private double commissionFee;
    private double ownerAmount;
    private String paymentMethod;
}

