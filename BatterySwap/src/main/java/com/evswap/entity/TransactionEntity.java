package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Integer id;

    @ManyToOne @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "StationID", nullable = false)
    private Station station;

    // Có thể null (theo DB)
    @Column(name = "PackageID")
    private Integer packageId;

    @Column(name = "TimeDate", nullable = false)
    private LocalDateTime timeDate;

    @Column(name = "Record")
    private String record;

    // CỘT thêm để hạch toán tiền (đã hướng dẫn ALTER TABLE)
    @Column(name = "Amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
