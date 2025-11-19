package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByUserId(String userId);
}
