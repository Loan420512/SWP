package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Booking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingID")
    private Integer id;

    @ManyToOne @JoinColumn(name = "StationID", nullable = false)
    private Station station;

    @ManyToOne @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "VehicleID", nullable = false)
    private Vehicle vehicle;

    @Column(name = "TimeDate", nullable = false)
    private LocalDateTime timeDate;

    // Bổ sung cho BR1/BR2
    @ManyToOne @JoinColumn(name = "BatteryID")
    private Battery battery;

    @Column(name = "EstimatedPrice", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "DepositAmount", precision = 10, scale = 2)
    private BigDecimal depositAmount;

    // PENDING/PAID/REFUNDED/WAIVED
    @Column(name = "DepositStatus", length = 20)
    private String depositStatus;

    // BOOKED/CANCELLED/COMPLETED
    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @Column(name = "CancelReason", length = 200)
    private String cancelReason;

    @Column(name = "CanceledAt")
    private LocalDateTime canceledAt;

    @OneToOne
    @JoinColumn(name = "DepositTxnID")
    private TransactionEntity depositTxn;
}
