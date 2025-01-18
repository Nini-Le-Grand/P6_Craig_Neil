package com.openclassroom.PayMyBuddy.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Represents a user in the PayMyBuddy application.
 *
 * <p>This entity maps to the {@code users} table in the database and contains fields
 * related to user authentication, account management, and auditing.</p>
 *
 * <h2>Fields:</h2>
 * <ul>
 *   <li>{@code id}: Unique identifier for the user.</li>
 *   <li>{@code username}: Unique username of the user.</li>
 *   <li>{@code email}: Unique email address of the user, validated for proper format.</li>
 *   <li>{@code password}: Encrypted password for user authentication.</li>
 *   <li>{@code balance}: Current account balance of the user.</li>
 *   <li>{@code created_at}: Timestamp when the user account was created (non-updatable).</li>
 *   <li>{@code updated_at}: Timestamp when the user account was last updated.</li>
 *   <li>{@code accountNonExpired}: Indicates if the account is expired.</li>
 *   <li>{@code accountNonLocked}: Indicates if the account is locked.</li>
 *   <li>{@code credentialsNonExpired}: Indicates if the credentials are expired.</li>
 *   <li>{@code enabled}: Indicates if the account is active.</li>
 *   <li>{@code roles}: A collection of roles assigned to the user for authorization.</li>
 * </ul>
 *
 * <h2>Lifecycle Callbacks:</h2>
 * <ul>
 *   <li>{@code onCreate()}: Initializes default values when the user is first persisted.</li>
 *   <li>{@code onUpdate()}: Updates the {@code updated_at} field whenever the user entity is modified.</li>
 * </ul>
 */
@Entity
@Table(name = "users")
@Data
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private LocalDateTime created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated_at;

    @Column(nullable = false)
    private boolean accountNonExpired;

    @Column(nullable = false)
    private boolean accountNonLocked;

    @Column(nullable = false)
    private boolean credentialsNonExpired;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = LocalDateTime.now();
        balance = BigDecimal.ZERO;
        accountNonExpired = true;
        accountNonLocked = true;
        credentialsNonExpired = true;
        enabled = true;
        roles.add("ROLE_USER");
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) () -> role)
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
