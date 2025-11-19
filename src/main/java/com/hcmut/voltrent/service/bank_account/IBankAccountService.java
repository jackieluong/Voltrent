package com.hcmut.voltrent.service.bank_account;

import com.hcmut.voltrent.dtos.request.BaseBankAccountRequest;
import com.hcmut.voltrent.dtos.response.BaseBankAccountResponse;

public interface IBankAccountService {

    BaseBankAccountResponse createBankAccount(BaseBankAccountRequest request);

    BaseBankAccountResponse getBankAccount(String companyId);

    BaseBankAccountResponse upsertBankAccount(BaseBankAccountRequest request);
}
