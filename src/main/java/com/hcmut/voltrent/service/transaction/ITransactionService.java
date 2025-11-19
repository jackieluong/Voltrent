package com.hcmut.voltrent.service.transaction;

import com.hcmut.voltrent.dtos.request.BaseTransactionRequest;
import com.hcmut.voltrent.dtos.response.BaseTransactionResponse;

public interface ITransactionService {
    BaseTransactionResponse createTransaction(BaseTransactionRequest request);

    double calculateCommissionFee(double totalAmount);
}
