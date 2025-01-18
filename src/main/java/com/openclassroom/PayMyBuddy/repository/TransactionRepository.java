package com.openclassroom.PayMyBuddy.repository;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link Transaction} entities.
 *
 * <p>Methods defined in this repository:</p>
 * <ul>
 *     <li>{@link #findAllByUserId(int)} - Retrieves all {@link Transaction}
 *     entities associated with a given user ID.</li>
 *     <li>{@link #findDistinctByUserOrReceiver(AppUser)} - Retrieves distinct
 *     {@link Transaction} entities where the user is either the sender or the
 *     receiver.</li>
 * </ul>
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    /**
     * Finds all {@link Transaction} entities associated with the specified user ID.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link Transaction} entities associated with the user
     */
    List<Transaction> findAllByUserId(int userId);

    /**
     * Finds distinct {@link Transaction} entities where the specified user is either
     * the sender or the receiver.
     *
     * @param connectedUser the user for whom to retrieve transactions
     * @return a list of distinct {@link Transaction} entities involving the user
     */
    @Query("SELECT DISTINCT t FROM Transaction t WHERE t.user = :connectedUser OR t.receiver = :connectedUser")
    List<Transaction> findDistinctByUserOrReceiver(AppUser connectedUser);
}
