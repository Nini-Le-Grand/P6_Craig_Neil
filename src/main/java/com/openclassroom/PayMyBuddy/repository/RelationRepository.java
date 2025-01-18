package com.openclassroom.PayMyBuddy.repository;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link Relation} entities.
 *
 * <p>Methods defined in this repository:</p>
 * <ul>
 *     <li>{@link #findAllByUserId(int)} - Retrieves all {@link Relation} entities associated with a given user ID.</li>
 *     <li>{@link #findByUserAndRelatedUser(AppUser, AppUser)} - Retrieves a specific {@link Relation} between two users.</li>
 * </ul>
 */
@Repository
public interface RelationRepository extends JpaRepository<Relation, Integer> {
    /**
     * Finds all {@link Relation} entities associated with the specified user ID.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link Relation} entities associated with the user
     */
    List<Relation> findAllByUserId(int userId);

    /**
     * Finds a specific {@link Relation} between a user and a related user.
     *
     * @param user the user entity
     * @param relation the related user entity
     * @return the {@link Relation} entity, or null if not found
     */
    Relation findByUserAndRelatedUser(AppUser user, AppUser relation);
}
