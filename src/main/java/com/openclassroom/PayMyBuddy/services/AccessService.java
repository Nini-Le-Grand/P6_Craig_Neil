package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.exceptions.*;
import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.models.RegistrationDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class AccessService {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    Validators validators;

    public void register(RegistrationDto registrationDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new RegistrationException("Le formulaire n'est pas renseigné correctement", registrationDto);
        }

        if (validators.usernameExists(registrationDto.getUsername())) {
            throw new RegistrationException("Ce nom d'utilisateur est déjà utilisé", registrationDto);
        }

        if (validators.emailExists(registrationDto.getEmail())) {
            throw new RegistrationException("Cet email est déjà utilisé", registrationDto);
        }

        if (!validators.isValidPassword(registrationDto.getPassword())) {
            throw new RegistrationException("MDP : 6 char minimum : 1 MAJ, 1 min, 1 chiffre", registrationDto);
        }

        if (!validators.passwordMatches(registrationDto.getPassword(), registrationDto.getConfirmPassword())) {
            throw new RegistrationException("Les mots de passe ne correspondent pas", registrationDto);
        }

        try {
            BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
            AppUser newUser = new AppUser();
            newUser.setUsername(registrationDto.getUsername());
            newUser.setEmail(registrationDto.getEmail());
            newUser.setPassword(bCryptEncoder.encode(registrationDto.getPassword()));
            newUser.getRoles().add("ROLE_USER");
            appUserRepository.save(newUser);
        } catch (Exception e) {
            throw new RegistrationException("Une erreur est survenue lors de la création", new RegistrationDto());
        }
    }
}
