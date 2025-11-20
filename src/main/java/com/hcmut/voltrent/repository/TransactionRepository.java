package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.time.Instant;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t JOIN t.booking b JOIN b.vehicle v WHERE v.ownerId = :ownerId AND t.createdAt BETWEEN :startDate AND :endDate")
    Page<Transaction> findTransactionsByOwnerAndDateRange(@Param("ownerId") String ownerId, @Param("startDate")  Instant startDate, @Param("endDate") Instant endDate, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t JOIN t.booking b JOIN b.vehicle v WHERE v.ownerId = :ownerId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsByOwnerAndDateRange(@Param("ownerId") String ownerId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT COALESCE(SUM(t.totalAmount), 0) FROM Transaction t JOIN t.booking b JOIN b.vehicle v WHERE v.ownerId = :ownerId AND t.createdAt BETWEEN :startDate AND :endDate")
    Long sumTotalAmountByOwnerAndDateRange(@Param("ownerId") String ownerId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT COALESCE(SUM(t.commissionFee), 0) FROM Transaction t JOIN t.booking b JOIN b.vehicle v WHERE v.ownerId = :ownerId AND t.createdAt BETWEEN :startDate AND :endDate")
    Long sumCommissionFeeByOwnerAndDateRange(@Param("ownerId") String ownerId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT COALESCE(SUM(t.ownerAmount), 0) FROM Transaction t JOIN t.booking b JOIN b.vehicle v WHERE v.ownerId = :ownerId AND t.createdAt BETWEEN :startDate AND :endDate")
    Long sumOwnerAmountByOwnerAndDateRange(@Param("ownerId") String ownerId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}

