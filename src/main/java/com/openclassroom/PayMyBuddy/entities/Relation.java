package com.openclassroom.PayMyBuddy.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "relations")
@Data
public class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "related_user_id", nullable = false)
    private AppUser relatedUser;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
    }
}