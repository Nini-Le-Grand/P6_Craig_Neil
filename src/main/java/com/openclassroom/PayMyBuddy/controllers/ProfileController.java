package com.openclassroom.PayMyBuddy.controllers;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.models.*;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import com.openclassroom.PayMyBuddy.services.ProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AppUserService appUserService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    public void setAttributes(Model model) {
        AppUser appUser = appUserService.getConnectedUser();

        model.addAttribute("username", appUser.getUsername());
        model.addAttribute("email", appUser.getEmail());
        model.addAttribute(new UpdateProfileDto());
        model.addAttribute(new UpdatePasswordDto());
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        logger.info("Processing GET /profile");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving profile page");
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Model model, @Valid @ModelAttribute UpdateProfileDto updateProfileDto, BindingResult result) {
        logger.info("Processing POST /profile");

        profileService.updateProfile(updateProfileDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Le profile a été mis à jour");

        logger.info("Retrieving profile page");
        return "profile";
    }

    @PostMapping("/profile/password")
    public String updatePassword(Model model, @Valid @ModelAttribute UpdatePasswordDto updatePasswordDto, BindingResult result) {
        logger.info("Processing POST /profile/password");

        profileService.updatePassword(updatePasswordDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Le mot de passe a été mis à jour");

        logger.info("Retrieving profile page");
        return "profile";
    }
}
