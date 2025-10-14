package com.evswap.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity @Table(name="Transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="TransactionID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UserID", nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="StationID", nullable=false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PackageID")
    private PackagePlan packagePlan; // có thể null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BookingID")
    private Booking booking; // liên kết giao dịch với booking

    @Column(name="Amount")    private BigDecimal amount;
    @Column(name="TimeDate")  private LocalDateTime transactionTime;
    @Column(name="Status")    private String status; // PENDING/SUCCESS/FAILED
    @Column(name="TransactionType") private String transactionType; // DEPOSIT/SWAP/REFUND/SUBSCRIPTION
}
