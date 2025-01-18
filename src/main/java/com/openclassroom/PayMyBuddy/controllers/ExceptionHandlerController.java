package com.openclassroom.PayMyBuddy.controllers;

import com.openclassroom.PayMyBuddy.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The {@code ExceptionHandlerController} class is a global exception handler for the application.
 *
 * <p>Supported exception handling includes:
 * <ul>
 *   <li>{@link RegistrationException}</li>
 *   <li>{@link BalanceException}</li>
 *   <li>{@link ProfileUpdateException}</li>
 *   <li>{@link RelationException}</li>
 *   <li>{@link TransactionException}</li>
 * </ul>
 * </p>
 *
 * <p>Each handler sets appropriate model attributes and redirects users to the relevant page.</p>
 */
@ControllerAdvice
public class ExceptionHandlerController {
    @Autowired
    private HomeController homeController;
    @Autowired
    private ProfileController profileController;
    @Autowired
    private RelationsController relationsController;
    @Autowired
    private TransactionController transactionController;
    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);

    /**
     * Handles {@link RegistrationException}.
     *
     * @param e     the exception instance
     * @param model the {@link Model} object to add attributes for the view
     * @return the registration view template
     */
    @ExceptionHandler(RegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRegistrationException(RegistrationException e, Model model) {
        logger.info("RegistrationException thrown: {}", e.getMessage());

        logger.info("Adding model attributes");
        model.addAttribute("registrationDto", e.getRegistrationDto());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving registration page");
        return "registration";
    }

    /**
     * Handles {@link BalanceException}.
     *
     * @param e     the exception instance
     * @param model the {@link Model} object to add attributes for the view
     * @return the home view template
     */
    @ExceptionHandler(BalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBalanceException(BalanceException e, Model model) {
        logger.info("BalanceException thrown: {}", e.getMessage());

        logger.info("Adding model attributes");
        homeController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving home page");
        return "home";
    }

    /**
     * Handles {@link ProfileUpdateException}.
     *
     * @param e     the exception instance
     * @param model the {@link Model} object to add attributes for the view
     * @return the profile view template
     */
    @ExceptionHandler(ProfileUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleProfileUpdateException(ProfileUpdateException e, Model model) {
        logger.info("ProfileUpdateException thrown: {}", e.getMessage());

        logger.info("Adding model attributes");
        profileController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving profile page");
        return "profile";
    }

    /**
     * Handles {@link RelationException}.
     *
     * @param e     the exception instance
     * @param model the {@link Model} object to add attributes for the view
     * @return the relations view template
     */
    @ExceptionHandler(RelationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNewRelationException(RelationException e, Model model) {
        logger.info("RelationException thrown: {}", e.getMessage());

        logger.info("Adding model attributes");
        relationsController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving relations page");
        return "relations";
    }

    /**
     * Handles {@link TransactionException}.
     *
     * @param e     the exception instance
     * @param model the {@link Model} object to add attributes for the view
     * @return the transaction view template
     */
    @ExceptionHandler(TransactionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTransactionException(TransactionException e, Model model) {
        logger.info("TransactionException thrown: {}", e.getMessage());

        logger.info("Adding model attributes");
        transactionController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving transaction page");
        return "transaction";
    }
}
