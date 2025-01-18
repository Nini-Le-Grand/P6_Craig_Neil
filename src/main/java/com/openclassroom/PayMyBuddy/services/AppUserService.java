package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service class for managing application users and user authentication.
 *
 * <p>This class implements {@link UserDetailsService} to provide user details for authentication</p>
 */
@Service
public class AppUserService implements UserDetailsService {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    /**
     * Retrieves the currently connected user from the security context.
     *
     * @return the currently authenticated {@link AppUser}
     */
    public AppUser getConnectedUser() {
        logger.info("Retrieving connected user");
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        int id = ((AppUser) authentication.getPrincipal()).getId();
        return appUserRepository.findById(id);
    }

    /**
     * Loads a user by their email address.
     *
     * @param email the email of the user to be loaded
     * @return the {@link UserDetails} of the user
     * @throws UsernameNotFoundException if no user is found with the provided email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by email: {}", email);
        AppUser appUser = appUserRepository.findByEmail(email);
        if (appUser == null) {
            logger.warn("User not found");
            throw new UsernameNotFoundException(email + " n'existe pas.");
        }
        return appUser;
    }

    /**
     * Updates the username of the specified user.
     *
     * @param appUser the user whose username is to be updated
     * @param newUsername the new username to be set
     */
    @Transactional
    public void updateUsername(AppUser appUser, String newUsername) {
        logger.info("Updating user username");
        appUser.setUsername(newUsername);
        appUserRepository.save(appUser);

        logger.info("User username updated: {}", newUsername);
    }

    /**
     * Updates the email of the specified user.
     *
     * @param appUser the user whose email is to be updated
     * @param newEmail the new email to be set
     */
    @Transactional
    public void updateEmail(AppUser appUser, String newEmail) {
        logger.info("Updating user email");
        appUser.setEmail(newEmail);
        appUserRepository.save(appUser);

        logger.info("User email updated: {}", newEmail);
    }

    /**
     * Updates the password of the specified user.
     *
     * @param appUser the user whose password is to be updated
     * @param newPassword the new password to be set
     */
    @Transactional
    public void updatePassword(AppUser appUser, String newPassword) {
        logger.info("Updating user password");
        String encodedPassword = passwordEncoder.encode(newPassword);
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        logger.info("User password updated: {}", encodedPassword);
    }

    /**
     * Updates the balance of the specified user.
     *
     * @param appUser the user whose balance is to be updated
     * @param amount the amount to be added to the current balance
     */
    @Transactional
    public void updateBalance(AppUser appUser, BigDecimal amount) {
        logger.info("Updating user balance");
        BigDecimal newAmount = appUser.getBalance().add(amount);
        appUser.setBalance(newAmount);
        appUserRepository.save(appUser);

        logger.info("User balance updated: {}", newAmount);
    }
}
