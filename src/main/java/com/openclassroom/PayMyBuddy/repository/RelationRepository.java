package com.openclassroom.PayMyBuddy.repository;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends JpaRepository<Relation, Integer> {
    List<Relation> findAllByUserId(int userId);

    Relation findByUserAndRelatedUser(AppUser user, AppUser relation);
}
