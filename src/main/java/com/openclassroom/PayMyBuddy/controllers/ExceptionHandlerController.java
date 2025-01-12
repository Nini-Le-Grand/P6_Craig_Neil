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

    @ExceptionHandler(RegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRegistrationException(RegistrationException e, Model model) {
        logger.info("Processing RegistrationException");

        logger.info("Adding model attributes");
        model.addAttribute("registrationDto", e.getRegistrationDto());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving registration page");
        return "registration";
    }

    @ExceptionHandler(BalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBalanceException(BalanceException e, Model model) {
        logger.info("Processing BalanceException");

        logger.info("Adding model attributes");
        homeController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving home page");
        return "home";
    }

    @ExceptionHandler(ProfileUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleProfileUpdateException(ProfileUpdateException e, Model model) {
        logger.info("Processing ProfileUpdateException");

        logger.info("Adding model attributes");
        profileController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving profile page");
        return "profile";
    }

    @ExceptionHandler(RelationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNewRelationException(RelationException e, Model model) {
        logger.info("Processing RelationException");

        logger.info("Adding model attributes");
        relationsController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving relations page");
        return "relations";
    }

    @ExceptionHandler(TransactionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTransactionException(TransactionException e, Model model) {
        logger.info("Processing TransactionException");

        logger.info("Adding model attributes");
        transactionController.setAttributes(model);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("success", false);

        logger.info("Retrieving transaction page");
        return "transaction";
    }
}
