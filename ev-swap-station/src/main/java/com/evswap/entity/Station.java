package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Table(name = "Station")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StationID")
    private Integer id;

    private String stationName;
    private String address;
    private String stationStatus;
    private String contact;

    // ✅ Constructor nhận ID
    public Station(Integer id) {
        this.id = id;
    }
}

