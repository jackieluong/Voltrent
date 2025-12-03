package com.hcmut.voltrent.service.report;

import com.hcmut.voltrent.dtos.response.PagedResponse;
import com.hcmut.voltrent.dtos.response.RevenueSummaryResponse;
import com.hcmut.voltrent.dtos.response.RevenueTransactionResponse;
import com.hcmut.voltrent.entity.Booking;
import com.hcmut.voltrent.entity.Payment;
import com.hcmut.voltrent.entity.Transaction;
import com.hcmut.voltrent.entity.User;
import com.hcmut.voltrent.exception.ConflictException;
import com.hcmut.voltrent.repository.*;
import com.hcmut.voltrent.security.SecurityUtil;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public RevenueSummaryResponse getRevenueSummary(Date startDate, Date endDate) {
        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));

        Long totalRevenue = transactionRepository.sumTotalAmountByOwnerAndDateRange(ownerId, startDate.toInstant(), endDate.toInstant());
        Long totalCommission = transactionRepository.sumCommissionFeeByOwnerAndDateRange(ownerId, startDate.toInstant(), endDate.toInstant());
        Long totalOwnerReceived = transactionRepository.sumOwnerAmountByOwnerAndDateRange(ownerId, startDate.toInstant(), endDate.toInstant());
        Long totalBookings = bookingRepository.countByOwnerAndDateRange(ownerId, startDate.toInstant(), endDate.toInstant());

        return RevenueSummaryResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue)
                .totalCommission(totalCommission)
                .totalOwnerReceived(totalOwnerReceived)
                .totalBookings(totalBookings)
                .build();
    }

    @Override
    public PagedResponse<RevenueTransactionResponse> getRevenueTransactions(Date startDate, Date endDate, int page, int size, String sort) {
        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));

        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null) {
            String[] sortParams = sort.split("_");
            String property = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
            if ("date".equals(property)) {
                property = "createdAt";
            } else if ("amount".equals(property)) {
                property = "totalAmount";
            }
            sorting = Sort.by(direction, property);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sorting);
        Page<Transaction> transactionPage = transactionRepository.findTransactionsByOwnerAndDateRange(ownerId, startDate.toInstant(), endDate.toInstant(), pageable);

        List<RevenueTransactionResponse> responses = transactionPage.getContent().stream()
                .map(this::toRevenueTransactionResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(new PageImpl<>(responses, pageable, transactionPage.getTotalElements()));
    }

    @Override
    public void exportRevenueTransactions(Date startDate, Date endDate, Writer writer) {
        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> transactions = transactionRepository.findTransactionsByOwnerAndDateRange(ownerId, startDate.toInstant(), endDate.toInstant());

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] header = {"Transaction ID", "Booking ID", "Vehicle Name", "Customer Name", "Completed At", "Total Amount", "Commission Fee", "Owner Amount", "Payment Method"};
            csvWriter.writeNext(header);
            for (Transaction transaction : transactions) {
                RevenueTransactionResponse response = toRevenueTransactionResponse(transaction);
                String[] data = {
                        response.getTransactionId().toString(),
                        response.getBookingId().toString(),
                        response.getVehicleName(),
                        response.getCustomerName(),
                        response.getCompletedAt().toString(),
                        String.valueOf(response.getTotalAmount()),
                        String.valueOf(response.getCommissionFee()),
                        String.valueOf(response.getOwnerAmount()),
                        response.getPaymentMethod()
                };
                csvWriter.writeNext(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error exporting revenue transactions to CSV", e);
        }
    }

    private RevenueTransactionResponse toRevenueTransactionResponse(Transaction transaction) {
        Booking booking = bookingRepository.findById(transaction.getBookingId())
                .orElseThrow(() -> new ConflictException("Booking not found"));

        String customerName = userRepository.findById(booking.getUserId())
                .map(User::getFullname)
                .orElse(null);

        String paymentMethod = paymentRepository.findByBookingId(booking.getId())
                .map(Payment::getGateway)
                .orElse(null);

        return RevenueTransactionResponse.builder()
                .transactionId(transaction.getId())
                .bookingId(booking.getId())
                .vehicleName(booking.getVehicle().getName())
                .customerName(customerName)
                .completedAt(transaction.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .totalAmount(transaction.getTotalAmount())
                .commissionFee(transaction.getCommissionFee())
                .ownerAmount(transaction.getOwnerAmount())
                .paymentMethod(paymentMethod)
                .build();
    }
}
