//package com.evswap.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Table(name = "Users")
//@Getter @Setter
//@NoArgsConstructor @AllArgsConstructor @Builder
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "UserID")
//    private Integer id;
//
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "Role")
//    private Role role;
//
//
//    private String username;
//    private String password;
//    private String fullName;
//    private String phone;
//    private String email;
////    private String role;
//    private String address;
//    private String status;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "StationID")
//    private Station station;
//
//    // ✅ Constructor thêm mới để BookingService dùng
//    public User(Integer id) {
//        this.id = id;
//    }
//}

package com.evswap.entity;

import com.evswap.entity.converter.RoleAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    // Dùng converter để tương thích 'Admin/Staff/Driver' trong DB/JSON
    @Convert(converter = RoleAttributeConverter.class)
    @Column(name = "Role")
    private Role role;

    @Column(name = "status")
    private String status;

    // Quan hệ tới Station – để LAZY.
    // QUAN TRỌNG: Chặn Jackson serialize để tránh lỗi ByteBuddy proxy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StationID")
    @JsonIgnore
    private Station station;
}
