package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Battery")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BatteryID")
    private Integer id;

    private String batteryName;
    private Double price;
    private String status;
    private String detailInformation;

    // Constructor nhận ID
    public Battery(Integer id) {
        this.id = id;
    }
}
