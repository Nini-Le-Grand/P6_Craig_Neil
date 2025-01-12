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

@Controller
public class AccessController {
    @Autowired
    private AccessService accessService;
    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);

    @GetMapping("/login")
    public String getLogin(Model model) {
        logger.info("Processing GET /login");

        logger.info("Adding model attributes");
        model.addAttribute(new LoginDto());

        logger.info("Retrieving login page");
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistration(Model model) {
        logger.info("Processing GET /registration");

        logger.info("Adding model attributes");
        model.addAttribute(new RegistrationDto());

        logger.info("Retrieving registration page");
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(Model model, @Valid @ModelAttribute RegistrationDto registrationDto, BindingResult result) {
        logger.info("Processing POST /registration");
        accessService.register(registrationDto, result);

        logger.info("Adding model attributes");
        model.addAttribute(new RegistrationDto());
        model.addAttribute("success", true);
        model.addAttribute("message", "Utilisateur créé! Veuillez vous connecter");

        logger.info("Retrieving registration page");
        return "registration";
    }
}