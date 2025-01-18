package com.openclassroom.PayMyBuddy.controllers;

import com.openclassroom.PayMyBuddy.models.LoginDto;
import com.openclassroom.PayMyBuddy.models.RegistrationDto;
import com.openclassroom.PayMyBuddy.services.AccessService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * The {@code AccessController} class is responsible for handling user access requests.
 * It interacts with the {@link AccessService} to process user data.
 *
 * <p>This controller handles:
 * <ul>
 *   <li>Displaying the login page</li>
 *   <li>Displaying the registration page</li>
 *   <li>Processing user registration</li>
 * </ul>
 */
@Controller
public class AccessController {
    @Autowired
    private AccessService accessService;
    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);

    /**
     * Handles the GET request for the login page.
     *
     * @param model the {@link Model} object used to add attributes required by the view
     * @return the name of the login view template
     */
    @GetMapping("/login")
    public String getLogin(Model model) {
        logger.info("Processing GET /login request");

        logger.info("Adding model attributes");
        model.addAttribute(new LoginDto());

        logger.info("Retrieving html login page");
        return "login";
    }

    /**
     * Handles the GET request for the registration page.
     *
     * @param model the {@link Model} object used to add attributes required by the view
     * @return the name of the registration view template
     */
    @GetMapping("/registration")
    public String getRegistration(Model model) {
        logger.info("Processing GET /registration request");

        logger.info("Adding model attributes");
        model.addAttribute(new RegistrationDto());

        logger.info("Retrieving html registration page");
        return "registration";
    }

    /**
     * Handles the POST request for user registration.
     *
     * @param model the {@link Model} object used to add attributes required by the view
     * @param registrationDto the {@link RegistrationDto} containing user registration data
     * @param result the {@link BindingResult} used to handle validation errors
     * @return the name of the registration view template
     */
    @PostMapping("/registration")
    public String registration(Model model, @Valid @ModelAttribute RegistrationDto registrationDto, BindingResult result) {
        logger.info("Processing POST /registration request");
        accessService.register(registrationDto, result);

        logger.info("Adding model attributes");
        model.addAttribute(new RegistrationDto());
        model.addAttribute("success", true);
        model.addAttribute("message", "Utilisateur créé! Veuillez vous connecter");

        logger.info("Retrieving html registration page");
        return "registration";
    }
}