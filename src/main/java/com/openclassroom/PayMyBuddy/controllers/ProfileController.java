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

/**
 * The {@code ProfileController} class handles operations related to the user's profile.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>Displaying the profile page</li>
 *   <li>Updating user profile details</li>
 *   <li>Changing the user's password</li>
 * </ul>
 * </p>
 *
 * <p>It interacts with :
 * <ul>
 *     <li>{@link ProfileService} for profile updates</li>
 *     <li>{@link AppUserService} to retrieve the current user details</li>
 * </ul>
 * </p>
 */
@Controller
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AppUserService appUserService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    /**
     * Sets common attributes for the model to be used in the profile page view.
     *
     * @param model the {@link Model} object to which attributes are added
     */
    public void setAttributes(Model model) {
        AppUser appUser = appUserService.getConnectedUser();

        model.addAttribute("username", appUser.getUsername());
        model.addAttribute("email", appUser.getEmail());
        model.addAttribute(new UpdateProfileDto());
        model.addAttribute(new UpdatePasswordDto());
    }

    /**
     * Handles the GET request to display the profile page.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @return the name of the profile page view template
     */
    @GetMapping("/profile")
    public String getProfile(Model model) {
        logger.info("Processing GET /profile request");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving html profile page");
        return "profile";
    }

    /**
     * Handles the POST request to update the user's profile information.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @param updateProfileDto the {@link UpdateProfileDto} containing the updated profile information
     * @param result the {@link BindingResult} object to handle validation results
     * @return the name of the profile page view template
     */
    @PostMapping("/profile")
    public String updateProfile(Model model, @Valid @ModelAttribute UpdateProfileDto updateProfileDto, BindingResult result) {
        logger.info("Processing POST /profile request");

        profileService.updateProfile(updateProfileDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Le profile a été mis à jour");

        logger.info("Retrieving html profile page");
        return "profile";
    }

    /**
     * Handles the POST request to change the user's password.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @param updatePasswordDto the {@link UpdatePasswordDto} containing the new password details
     * @param result the {@link BindingResult} object to handle validation results
     * @return the name of the profile page view template
     */
    @PostMapping("/profile/password")
    public String updatePassword(Model model, @Valid @ModelAttribute UpdatePasswordDto updatePasswordDto, BindingResult result) {
        logger.info("Processing POST /profile/password request");

        profileService.updatePassword(updatePasswordDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Le mot de passe a été mis à jour");

        logger.info("Retrieving html profile page");
        return "profile";
    }
}
