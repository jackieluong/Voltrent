package com.hcmut.voltrent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BankAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;
    @Column(name = "account_name")
    private String accountName;
    @Column(name = "bank_code")
    private String bankCode;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "user_id", nullable = false)
    private String userId;

}
