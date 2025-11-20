package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.response.PagedResponse;
import com.hcmut.voltrent.dtos.response.RevenueSummaryResponse;
import com.hcmut.voltrent.dtos.response.RevenueTransactionResponse;
import com.hcmut.voltrent.service.report.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/api/reports/revenue")
@RequiredArgsConstructor
public class ReportController {
    private final IReportService reportService;

    @GetMapping("/summary")
    @Operation(summary = "Get revenue summary", description = "Returns revenue summary for the logged-in user's company")
    public ResponseEntity<RevenueSummaryResponse> getRevenueSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {

        RevenueSummaryResponse response = reportService.getRevenueSummary(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get revenue transactions", description = "Returns a paginated list of transactions for the logged-in user's company")
    public ResponseEntity<PagedResponse<RevenueTransactionResponse>> getRevenueTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        PagedResponse<RevenueTransactionResponse> response = reportService.getRevenueTransactions(startDate, endDate, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/export")
    @Operation(summary = "Export revenue transactions to CSV", description = "Returns a CSV file of revenue transactions for the logged-in user's company")
    public void exportRevenueTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"revenue_transactions.csv\"");
        reportService.exportRevenueTransactions(startDate, endDate, response.getWriter());
    }
}

