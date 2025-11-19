package com.hcmut.voltrent.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_payment_info" )
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompanyPaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "company_id", unique = true)
    @JsonAlias({"user_id"})
    private String companyId;

    private String address;
    private String website;
    private String logo;
    private String description;

    private String paymentQRUrl;

    private String accountNumber;
    private String accountName;
    private String bankCode;

}
