package com.openclassroom.PayMyBuddy.repository;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByUserId(int userId);

    @Query("SELECT DISTINCT t FROM Transaction t WHERE t.user = :connectedUser OR t.receiver = :connectedUser")
    List<Transaction> findDistinctByUserOrReceiver(AppUser connectedUser);
}
