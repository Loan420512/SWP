package com.evswap.evswapstation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    @Column(nullable = false, unique = true, length = 50)
    private String userName;

    @Column(nullable = false, length = 100)
    private String password;
    @Column(name = "FullName")
    private String fullName;
    private String phone;
    private String email;

    @Column(length = 20)
    private String role;

    private String address;

    // Quan hệ: 1 User có nhiều Vehicles
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;
}
