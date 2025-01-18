package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.exceptions.*;
import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.models.RegistrationDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

/**
 * Service class for handling user registration and access management.
*/
@Service
public class AccessService {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    Validators validators;

    private static final Logger logger = LoggerFactory.getLogger(AccessService.class);

    /**
     * Registers a new user with the provided registration data.
     *
     * <p>This method validates the registration data.
     * If all validations pass, the new user is created and saved to the database.</p>
     *
     * @param registrationDto the registration data transfer object containing user information
     * @param result the BindingResult object to hold validation errors
     * @throws RegistrationException if the registration data is invalid or if an error occurs during registration
     */
    public void register(RegistrationDto registrationDto, BindingResult result) {
        logger.info("Processing register() method");
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
            logger.info("Mapping newUser");
            BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
            AppUser newUser = new AppUser();
            newUser.setUsername(registrationDto.getUsername());
            newUser.setEmail(registrationDto.getEmail());
            newUser.setPassword(bCryptEncoder.encode(registrationDto.getPassword()));
            newUser.getRoles().add("ROLE_USER");

            appUserRepository.save(newUser);
            logger.info("New user saved :{}", newUser);
        } catch (Exception e) {
            throw new RegistrationException("Une erreur est survenue lors de la création", new RegistrationDto());
        }
    }
}
