package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="Inventory",
        uniqueConstraints=@UniqueConstraint(columnNames={"StationID","BatteryID"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="InventoryID") private Integer id;

    @ManyToOne @JoinColumn(name="StationID", nullable=false)
    private Station station;

    @ManyToOne @JoinColumn(name="BatteryID", nullable=false)
    private Battery battery;

    @Column(name="Status") private String status; // AVAILABLE/RESERVED/SWAPPED_OUT (quy ước)
}
