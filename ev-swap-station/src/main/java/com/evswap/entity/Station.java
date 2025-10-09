package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StationID")
    private Integer id;

    private String stationName;
    private String address;
    private String stationStatus;
    private String contact;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    private List<Inventory> inventories;
}
