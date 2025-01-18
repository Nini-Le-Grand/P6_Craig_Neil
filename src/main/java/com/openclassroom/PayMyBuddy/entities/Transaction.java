package com.openclassroom.PayMyBuddy.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction between users in the PayMyBuddy application.
 *
 * <p>This entity maps to the {@code transactions} table in the database and captures
 * the details of transactions.</p>
 *
 * <h2>Fields:</h2>
 * <ul>
 *   <li>{@code id}: Unique identifier for the transaction.</li>
 *   <li>{@code user}: The user who initiated the transaction.</li>
 *   <li>{@code receiver}: The user who receives the transaction.</li>
 *   <li>{@code description}: A brief description of the transaction.</li>
 *   <li>{@code amount}: The amount of money transferred in the transaction.</li>
 *   <li>{@code created_at}: Timestamp indicating when the transaction was created.</li>
 * </ul>
 *
 * <h2>Lifecycle Callbacks:</h2>
 * <ul>
 *   <li>{@code onCreate()}: Initializes the {@code created_at} field with the current timestamp
 *   when the transaction is first persisted.</li>
 * </ul>
 */
@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private AppUser receiver;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
    }
}