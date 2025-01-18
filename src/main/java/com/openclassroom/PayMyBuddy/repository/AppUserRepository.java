package com.openclassroom.PayMyBuddy.repository;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link AppUser} entities.
 *
 * <p>Methods defined in this repository:</p>
 * <ul>
 *     <li>{@link #findById(int)} - Retrieves an {@link AppUser} by their unique identifier.</li>
 *     <li>{@link #findByEmail(String)} - Retrieves an {@link AppUser} by their email address.</li>
 *     <li>{@link #findByUsername(String)} - Retrieves an {@link AppUser} by their username.</li>
 * </ul>
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    /**
     * Finds an {@link AppUser} by their unique identifier.
     *
     * @param Id the unique identifier of the user
     * @return the {@link AppUser} entity, or null if not found
     */
    AppUser findById(int Id);

    /**
     * Finds an {@link AppUser} by their email address.
     *
     * @param email the email address of the user
     * @return the {@link AppUser} entity, or null if not found
     */
    AppUser findByEmail(String email);

    /**
     * Finds an {@link AppUser} by their username.
     *
     * @param username the username of the user
     * @return the {@link AppUser} entity, or null if not found
     */
    AppUser findByUsername(String username);
}
