package com.hcmut.voltrent.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueSummaryResponse {
    private Date startDate;
    private Date endDate;
    private Long totalRevenue;
    private Long totalCommission;
    private Long totalOwnerReceived;
    private Long totalBookings;
}
