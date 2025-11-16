package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.CompanyPaymentInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CompanyPaymentInfoRepository extends JpaRepository<CompanyPaymentInfo, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE CompanyPaymentInfo ci " +
            "SET ci.paymentQRUrl = ?1 " +
            "WHERE ci.companyId = ?2" )
    void updateQRPaymentUrl(String qrPaymentUrl, String companyId);
}
