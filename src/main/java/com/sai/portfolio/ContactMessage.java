package com.sai.portfolio;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false, length = 60) public String name;
    @Column(nullable = false, length = 120) public String email;
    @Column(name = "message_text", nullable = false, length = 2000) public String message;
    @Column(nullable = false) public Instant createdAt = Instant.now();
}
