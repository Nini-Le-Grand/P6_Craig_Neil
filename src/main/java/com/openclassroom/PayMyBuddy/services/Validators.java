package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service class for validating user inputs and conditions.
 *
 * <p>This class provides various validation methods to check the validity of data.</p>
 */
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

    private static final Logger logger = LoggerFactory.getLogger(Validators.class);

    /**
     * Checks if a username already exists in the system.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        logger.info("Checking if username exists");
        return appUserRepository.findByUsername(username) != null;
    }

    /**
     * Checks if an email already exists in the system.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        logger.info("Checking if email exists");
        return appUserRepository.findByEmail(email) != null;
    }

    /**
     * Validates the format of a password.
     *
     * <p>The password must be at least 6 characters long and include at least one uppercase letter,
     * one lowercase letter, and one digit.</p>
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        logger.info("Checking if password is valid");
        return password.length() >= 6 && password.matches(".*[A-Z].*") && password.matches(
                ".*[a-z].*") && password.matches(".*\\d.*");
    }

    /**
     * Checks if two passwords match.
     *
     * @param password the password to compare
     * @param confirmPassword the password confirmation to compare
     * @return true if the passwords match, false otherwise
     */
    public boolean passwordMatches(String password, String confirmPassword) {
        logger.info("Checking if password matches");
        return password.equals(confirmPassword);
    }

    /**
     * Checks if the provided password matches the current user's password.
     *
     * @param password the password to check
     * @return true if the password is correct, false otherwise
     */
    public boolean checkPassword(String password) {
        logger.info("Checking if password is correct");
        AppUser appUser = appUserService.getConnectedUser();
        return passwordEncoder.matches(password, appUser.getPassword());
    }

    /**
     * Checks if a relation exists between two users.
     *
     * @param user the user initiating the check
     * @param relation the user to check the relation against
     * @return true if the relation exists, false otherwise
     */
    public boolean relationExists(AppUser user, AppUser relation) {
        logger.info("Checking if relation exists");
        return relationRepository.findByUserAndRelatedUser(user, relation) != null;
    }

    /**
     * Checks if the resulting balance after a transaction would be negative.
     *
     * @param amount the amount to be checked against the user's balance
     * @return true if the balance would be negative, false otherwise
     */
    public boolean isBalanceNegative(BigDecimal amount) {
        logger.info("Checking if balance is negative");
        AppUser appUser = appUserService.getConnectedUser();
        return appUser.getBalance()
                .subtract(amount)
                .compareTo(BigDecimal.ZERO) < 0;
    }
}
