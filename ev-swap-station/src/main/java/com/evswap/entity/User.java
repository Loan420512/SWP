package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer id;


    @Enumerated(EnumType.STRING)
    @Column(name = "Role")
    private Role role;


    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String email;
//    private String role;
    private String address;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StationID")
    private Station station;

    // ✅ Constructor thêm mới để BookingService dùng
    public User(Integer id) {
        this.id = id;
    }
}
