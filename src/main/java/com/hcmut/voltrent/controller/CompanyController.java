package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.model.RestResponse;
import com.hcmut.voltrent.dtos.request.BaseBankAccountRequest;
import com.hcmut.voltrent.dtos.response.BaseBankAccountResponse;
import com.hcmut.voltrent.security.SecurityUtil;
import com.hcmut.voltrent.service.bank_account.IBankAccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/company")
public class CompanyController {
    private IBankAccountService bankAccountService;

    public CompanyController(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/bank-account")
    public ResponseEntity<?> createBankAccount(@Valid @RequestBody BaseBankAccountRequest request) {

        BaseBankAccountResponse response = bankAccountService.createBankAccount(request);
        return RestResponse.successResponse("Create bank account successfully", response);
    }

    @GetMapping("/bank-account")
    public ResponseEntity<?> getBankAccount() {
        String companyId = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user"));
        BaseBankAccountResponse response = bankAccountService.getBankAccount(companyId);
        return RestResponse.successResponse("Get bank account sucessfully", response);
    }

    @PutMapping("/bank-account")
    public ResponseEntity<?> updateCompanyBankAccount(@Valid @RequestBody BaseBankAccountRequest request) {

        BaseBankAccountResponse response = bankAccountService.upsertBankAccount(request);
        return RestResponse.successResponse("Updating bank account successfully", response);
    }
}
