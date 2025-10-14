package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Table(name = "Vehicle")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VehicleID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    @JsonIgnore
    private User user;

    private String vin;
    private String vehicleModel;
    private String batteryType;
    private String registerInformation;

    // ✅ Constructor nhận ID
    public Vehicle(Integer id) {
        this.id = id;
    }
}
