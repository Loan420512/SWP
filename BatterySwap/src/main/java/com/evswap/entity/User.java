package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users") // đúng y tên bảng trong SQL Server
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer id;

    @Column(name = "UserName", nullable = false, unique = true)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role")
    private Role role; // Driver/Staff/Admin

    @Column(name = "FullName") private String fullName;
    @Column(name = "Phone")    private String phone;
    @Column(name = "Email")    private String email;
    @Column(name = "Address")  private String address;
}
