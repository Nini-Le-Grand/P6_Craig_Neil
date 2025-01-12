package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.exceptions.ProfileUpdateException;
import com.openclassroom.PayMyBuddy.models.*;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Objects;

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

    public UserProfileDto getProfile(AppUser user) {
        UserProfileDto profile = new UserProfileDto();
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        return profile;
    }

    public void updateProfile(UpdateProfileDto updateProfileDto, BindingResult result) {
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
        } catch (Exception e) {
            throw new ProfileUpdateException("Une erreur s'est produite lors de la modification du profile");
        }
    }

    public void updatePassword(UpdatePasswordDto updatePasswordDto, BindingResult result) {
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
        } catch (Exception e) {
            throw new ProfileUpdateException("Une erreur s'est produite lors de la modification du profile");
        }
    }
}