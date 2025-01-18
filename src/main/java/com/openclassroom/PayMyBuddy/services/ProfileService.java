package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.exceptions.ProfileUpdateException;
import com.openclassroom.PayMyBuddy.models.*;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Objects;

/**
 * Service class for managing user profile operations.
 */
@Service
public class ProfileService {
    @Autowired
    Validators validators;
    @Autowired
    AppUserService appUserService;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    /**
     * Retrieves the profile information for the specified user.
     *
     * <p>This method creates a {@link UserProfileDto} containing the user's username
     * and email.</p>
     *
     * @param user the user whose profile information is to be retrieved
     * @return a {@link UserProfileDto} containing the user's profile information
     */
    public UserProfileDto getProfile(AppUser user) {
        logger.info("Retrieving profile information");
        UserProfileDto profile = new UserProfileDto();
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        return profile;
    }

    /**
     * Updates the user's profile information based on the provided data.
     *
     * <p>This method validates the input, checks for existing usernames and emails,
     * and updates the user's profile accordingly.</p>
     *
     * @param updateProfileDto the DTO containing updated profile information
     * @param result          the BindingResult object containing validation results
     * @throws ProfileUpdateException if there are validation errors or if an error occurs during the update process
     */
    public void updateProfile(UpdateProfileDto updateProfileDto, BindingResult result) {
        logger.info("Processing updateProfile() method");
        AppUser appUser = appUserService.getConnectedUser();

        if (result.hasErrors()) {
            throw new ProfileUpdateException("Le formulaire n'est pas renseigné correctement");
        }

        if (!Objects.equals(updateProfileDto.getUsername(), "")) {
            if (validators.usernameExists(updateProfileDto.getUsername())) {
                throw new ProfileUpdateException("Le username existe déjà");
            }
            appUser.setUsername(updateProfileDto.getUsername());
        }

        if (!Objects.equals(updateProfileDto.getEmail(), "")) {
            if (validators.emailExists(updateProfileDto.getEmail())) {
                throw new ProfileUpdateException("L'email existe déjà");
            }
            appUser.setEmail(updateProfileDto.getEmail());
        }

        try {
            appUserRepository.save(appUser);
            logger.info("User profile updated");
        } catch (Exception e) {
            throw new ProfileUpdateException("Une erreur s'est produite lors de la modification du profile");
        }
    }

    /**
     * Updates the user's password based on the provided data.
     *
     * <p>This method validates the current password, checks the validity of the new
     * password, and updates the password if all validations pass.</p>
     *
     * @param updatePasswordDto the DTO containing the old and new password information
     * @param result           the BindingResult object containing validation results
     * @throws ProfileUpdateException if there are validation errors or if an error occurs during the update process
     */
    public void updatePassword(UpdatePasswordDto updatePasswordDto, BindingResult result) {
        logger.info("Processing updatePassword() method");
        if (result.hasErrors()) {
            throw new ProfileUpdateException("Le formulaire n'est pas renseigné correctement");
        }

        if (!validators.checkPassword(updatePasswordDto.getOldPassword())) {
            throw new ProfileUpdateException("le mot de passe n'est pas correct");
        }

        if (!validators.isValidPassword(updatePasswordDto.getNewPassword())) {
            throw new ProfileUpdateException("MDP : 6 char minimum : 1 MAJ, 1 min, 1 chiffre");
        }

        if (!validators.passwordMatches(updatePasswordDto.getNewPassword(), updatePasswordDto.getConfirmPassword())) {
            throw new ProfileUpdateException("Les mots de passe ne correspondent pas");
        }

        try {
            String hashedPassword = passwordEncoder.encode(updatePasswordDto.getNewPassword());
            AppUser appUser = appUserService.getConnectedUser();
            appUser.setPassword(hashedPassword);
            appUserRepository.save(appUser);
            logger.info("User password updated");
        } catch (Exception e) {
            throw new ProfileUpdateException("Une erreur s'est produite lors de la modification du profile");
        }
    }
}