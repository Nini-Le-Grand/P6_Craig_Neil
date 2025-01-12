package com.openclassroom.PayMyBuddy.repository;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    AppUser findById(int Id);

    AppUser findByEmail(String email);

    AppUser findByUsername(String username);
}
