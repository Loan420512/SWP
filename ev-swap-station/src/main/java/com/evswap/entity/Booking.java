package com.evswap.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity @Table(name="Booking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="BookingID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UserID", nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="StationID", nullable=false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="VehicleID")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BatteryID")
    private Battery battery; // loại pin giữ chỗ

    @Column(name="TimeDate")
    private LocalDateTime timeDate;

    // BR1
    @Column(name="EstimatedPrice") private BigDecimal estimatedPrice;
    @Column(name="DepositAmount")  private BigDecimal depositAmount;
    @Column(name="DepositStatus")  private String depositStatus; // PENDING/PAID/REFUNDED
    @Column(name="DepositTxnID")   private String depositTxnId;

    // lifecycle
    @Column(name="Status")    private String status;    // BOOKED/ARRIVED/SWAPPED/CANCELLED/NO_SHOW
    @Column(name="HoldUntil") private LocalDateTime holdUntil;

    // BR2 cancel
    @Column(name="CancelReason") private String cancelReason;
    @Column(name="CanceledAt")   private LocalDateTime canceledAt;

    // để dành nếu về sau theo dõi từng viên pin
    @Column(name="OldUnitID") private Long oldUnitId;
    @Column(name="NewUnitID") private Long newUnitId;
}
