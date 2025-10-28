package com.evswap.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Inventory")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InventoryID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StationID")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BatteryID")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Battery battery;

    @Column(name = "ReadyQty")
    private Integer readyQty;

    @Column(name = "HoldQty")
    private Integer holdQty;

    @Column(name = "ChargingQty")
    private Integer chargingQty;

    @Column(name = "MaintenanceQty")
    private Integer maintenanceQty;

    @Column(name = "Status")
    private String status;
}
