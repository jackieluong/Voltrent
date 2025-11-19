package com.hcmut.voltrent.service.transaction;

import com.hcmut.voltrent.dtos.request.BaseTransactionRequest;
import com.hcmut.voltrent.dtos.response.BaseTransactionResponse;
import com.hcmut.voltrent.entity.Transaction;
import com.hcmut.voltrent.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    public static double commissionPercent;

    public TransactionService(TransactionRepository transactionRepository,
                              @Value("${booking.commission-percent}") double commissionPercent) {
        this.transactionRepository = transactionRepository;
        this.commissionPercent = commissionPercent;
    }

    @Override
    public BaseTransactionResponse createTransaction(BaseTransactionRequest request) {
        double commissionFee = calculateCommissionFee(request.getTotalAmount());
        Transaction transaction = Transaction.builder()
                .bookingId(request.getBookingId())
                .totalAmount(request.getTotalAmount())
                .commissionFee(commissionFee)
                .ownerAmount(request.getTotalAmount() - commissionFee)
                .build();

        try {
            Transaction saved = transactionRepository.save(transaction);
            log.info("Saving transaction successfully {}", saved);
            return BaseTransactionResponse.builder()
                    .transactionId(String.valueOf(saved.getId()))
                    .totalAmount(saved.getTotalAmount())
                    .commissionFee(saved.getCommissionFee())
                    .ownerAmount(saved.getOwnerAmount())
                    .build();
        } catch (Exception e) {
            log.error("Error creating transaction {}", transaction, e);
            throw new RuntimeException("Error creating transaction");
        }

    }

    @Override
    public double calculateCommissionFee(double totalAmount) {
        return totalAmount * commissionPercent;
    }
}
