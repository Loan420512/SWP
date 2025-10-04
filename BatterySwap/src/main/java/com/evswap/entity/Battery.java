package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="Battery")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Battery {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="BatteryID") private Integer id;

    @Column(name="BatteryName") private String name;
    @Column(name="Price", precision=10, scale=2) private BigDecimal price;
    @Column(name="Status") private String status; // Full/Empty/Damaged/Maintenance
    @Column(name="DetailInformation") private String detailInformation;
}
