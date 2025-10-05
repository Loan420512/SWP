package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Vehicle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VehicleID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "VIN", nullable = false, unique = true)
    private String vin;

    @Column(name = "VehicleModel")
    private String vehicleModel;

    @Column(name = "BatteryType")
    private String batteryType;

    @Column(name = "RegisterInformation")
    private String registerInformation;
}
