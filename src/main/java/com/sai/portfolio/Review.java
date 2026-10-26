package com.sai.portfolio;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reviews")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false, length = 60) public String name;
    @Column(nullable = false, length = 120) public String email;
    @Column(nullable = false) public int rating;
    @Column(name = "review_text", nullable = false, length = 1000) public String text;
    @Column(nullable = false) public Instant createdAt = Instant.now();
}
