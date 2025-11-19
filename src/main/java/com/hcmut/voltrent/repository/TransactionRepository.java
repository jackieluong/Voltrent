package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
