package com.hcmut.voltrent.service.report;

import com.hcmut.voltrent.dtos.response.PagedResponse;
import com.hcmut.voltrent.dtos.response.RevenueSummaryResponse;
import com.hcmut.voltrent.dtos.response.RevenueTransactionResponse;

import java.io.Writer;
import java.util.Date;

public interface IReportService {
    RevenueSummaryResponse getRevenueSummary(Date startDate, Date endDate);
    PagedResponse<RevenueTransactionResponse> getRevenueTransactions(Date startDate, Date endDate, int page, int size, String sort);
    void exportRevenueTransactions(Date startDate, Date endDate, Writer writer);
}
