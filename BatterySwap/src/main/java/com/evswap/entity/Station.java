package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="Station")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Station {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="StationID") private Integer id;
    @Column(name="StationName",nullable=false) private String name;
    @Column(name="Address") private String address;
    @Column(name="StationStatus") private String stationStatus; // Open/Closed
    @Column(name="Contact") private String contact;
}
