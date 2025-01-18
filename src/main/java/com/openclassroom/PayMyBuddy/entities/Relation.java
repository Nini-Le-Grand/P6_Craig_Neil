package com.openclassroom.PayMyBuddy.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a relationship between two users in the PayMyBuddy application.
 *
 * <p>This entity maps to the {@code relations} table in the database and is used
 * to define relations between users. Each relation consists of a user
 * and a related user.</p>
 *
 * <h2>Fields:</h2>
 * <ul>
 *   <li>{@code id}: Unique identifier for the relation.</li>
 *   <li>{@code user}: The user who initiated or owns the relation.</li>
 *   <li>{@code relatedUser}: The user who is connected to the initiating user.</li>
 *   <li>{@code created_at}: Timestamp indicating when the relation was created.</li>
 * </ul>
 *
 * <h2>Lifecycle Callbacks:</h2>
 * <ul>
 *   <li>{@code onCreate()}: Initializes the {@code created_at} field with the current timestamp
 *   when the relation is first persisted.</li>
 * </ul>
 */
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