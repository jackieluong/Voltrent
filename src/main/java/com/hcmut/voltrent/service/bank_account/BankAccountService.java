package com.hcmut.voltrent.service.bank_account;

import com.hcmut.voltrent.dtos.request.BaseBankAccountRequest;
import com.hcmut.voltrent.dtos.response.BaseBankAccountResponse;
import com.hcmut.voltrent.entity.BankAccount;
import com.hcmut.voltrent.exception.ConflictException;
import com.hcmut.voltrent.repository.BankAccountRepository;
import com.hcmut.voltrent.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class BankAccountService implements IBankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper modelMapper;

    public BankAccountService(BankAccountRepository bankAccountRepository, ModelMapper modelMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BaseBankAccountResponse createBankAccount(BaseBankAccountRequest request) {

        String companyId = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user"));

        bankAccountRepository.findByUserId(companyId)
                .ifPresent(bankAccount -> {
                    log.info("Bank account already exists with companyId: {}, returning existing bank account", companyId);
                    throw new ConflictException("Bank account already exists");
                });

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountName(request.getAccountName());
        bankAccount.setAccountNumber(request.getAccountNumber());
        bankAccount.setBankCode(request.getBankCode());
        bankAccount.setBankName(request.getBankName());
        bankAccount.setUserId(companyId);

        try {
            BankAccount saved = bankAccountRepository.save(bankAccount);
            return BaseBankAccountResponse.builder()
                    .accountNumber(saved.getAccountNumber())
                    .accountName(saved.getAccountName())
                    .bankCode(saved.getBankCode())
                    .bankName(saved.getBankName())
                    .id(String.valueOf(saved.getId()))
                    .updatedAt(LocalDateTime.now().toString())
                    .build();
        } catch (Exception e) {
            log.error("Error creating bank account {}", bankAccount, e);
            throw new RuntimeException("Error creating bank account");
        }
    }

    @Override
    public BaseBankAccountResponse getBankAccount(String companyId) {
        BankAccount bankAccount = bankAccountRepository.findByUserId(companyId)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        log.info("Bank account found {}", bankAccount);
        return BaseBankAccountResponse.builder()
                .accountNumber(bankAccount.getAccountNumber())
                .accountName(bankAccount.getAccountName())
                .bankCode(bankAccount.getBankCode())
                .bankName(bankAccount.getBankName())
                .id(String.valueOf(bankAccount.getId()))
                .updatedAt(LocalDateTime.now().toString())
                .build();
    }

    @Override
    public BaseBankAccountResponse upsertBankAccount(BaseBankAccountRequest request) {
        String companyId = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user"));

        BankAccount account = bankAccountRepository.findByUserId(companyId)
                .map(bankAccount -> {
                    log.info("Bank account already exists with companyId: {}, updating bank account", companyId);
                    return bankAccount;
                })
                .orElseGet(() -> {
                    log.info("Bank account not found with companyId: {}, creating new bank account", companyId);
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setUserId(companyId);
                    return bankAccount;
                });

        account.setAccountName(request.getAccountName());
        account.setAccountNumber(request.getAccountNumber());
        account.setBankCode(request.getBankCode());
        account.setBankName(request.getBankName());
        try {
            bankAccountRepository.save(account);
        } catch (Exception e) {
            log.error("Error upsert bank account {}", account, e);
            throw new RuntimeException("Error upsert bank account");
        }
        return BaseBankAccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .bankCode(account.getBankCode())
                .bankName(account.getBankName())
                .id(String.valueOf(account.getId()))
                .updatedAt(LocalDateTime.now().toString())
                .build();
    }


}
