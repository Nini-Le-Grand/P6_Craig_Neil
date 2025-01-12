package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class Validators {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    RelationRepository relationRepository;
    @Autowired
    AppUserService appUserService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public boolean usernameExists(String username) {
        return appUserRepository.findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        return appUserRepository.findByEmail(email) != null;
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[A-Z].*") && password.matches(
                ".*[a-z].*") && password.matches(".*\\d.*");
    }

    public boolean passwordMatches(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public boolean checkPassword(String password) {
        AppUser appUser = appUserService.getConnectedUser();
        return passwordEncoder.matches(password, appUser.getPassword());
    }

    public boolean relationExists(AppUser user, AppUser relation) {
        return relationRepository.findByUserAndRelatedUser(user, relation) != null;
    }

    public boolean isBalanceNegative(BigDecimal amount) {
        AppUser appUser = appUserService.getConnectedUser();
        return appUser.getBalance()
                .subtract(amount)
                .compareTo(BigDecimal.ZERO) < 0;
    }
}
