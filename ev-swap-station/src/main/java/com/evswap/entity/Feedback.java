package com.evswap.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với User
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    // Liên kết với Station (nếu feedback về trạm)
    @ManyToOne
    @JoinColumn(name = "StationID")
    private Station station;

    private String content;
    private int rating; // 1-5 stars
}
