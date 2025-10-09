package com.evswap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với User
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "StationID", nullable = false)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "PackageID", nullable = false)
    private PackagePlan packagePlan;


    private Double amount;
    private LocalDateTime transactionTime;
    private String status; // SUCCESS, FAILED, PENDING
    private String method; // CREDIT_CARD, MOMO, ZALOPAY
}
