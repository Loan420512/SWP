package com.evswap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "Booking")
@Data // <-- Thêm dòng này để Lombok tự sinh getter/setter/toString/equals/hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với User
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    // Quan hệ với Station
    @ManyToOne
    @JoinColumn(name = "StationID", nullable = false)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "VehicleID")
    private Vehicle vehicle;

    private LocalDateTime timeDate;


    private LocalDateTime bookingTime;
    private String status; // PENDING, CONFIRMED, CANCELLED
}
