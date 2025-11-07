package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PackagePlans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PackageID")
    private Integer id;

    @Column(name = "PackageName")
    private String planName;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price")
    private Double price;

    @Column(name = "DurationDays")
    private Integer durationDays;
}
