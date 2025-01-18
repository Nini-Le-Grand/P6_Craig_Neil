package com.openclassroom.PayMyBuddy.controllers;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.models.BalanceDto;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import com.openclassroom.PayMyBuddy.services.HomeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * The {@code HomeController} class is responsible for managing operations related to the user's home page.
 *
 * <p>This controller handles:
 * <ul>
 *   <li>Displaying the home page</li>
 *   <li>Processing credit transactions</li>
 *   <li>Processing withdrawal transactions</li>
 * </ul>
 * </p>
 *
 * <p>It interacts with :
 * <ul>
 *     <li>{@link AppUserService} to retrieve the connected user</li>
 *     <li>{@link HomeService} to process balance-related operations</li>
 * </ul>
 * </p>
 */
@Controller
public class HomeController {
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private HomeService homeService;
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Sets common attributes for the model to be used in the home page view.
     *
     * @param model the {@link Model} object to which attributes are added
     */
    public void setAttributes(Model model) {
        AppUser appUser = appUserService.getConnectedUser();
        List<Transaction> operations = homeService.getUserOperation();

        model.addAttribute("username", appUser.getUsername());
        model.addAttribute("balance", appUser.getBalance());
        model.addAttribute("operations", operations);
        model.addAttribute(new BalanceDto());
    }

    /**
     * Handles the GET request to display the home page.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @return the name of the home page view template
     */
    @GetMapping("/home")
    public String getHomePage(Model model) {
        logger.info("Processing GET /home request");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving html home page");
        return "home";
    }

    /**
     * Handles the POST request to credit the user's account balance.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @param balanceDto the {@link BalanceDto} object containing the credit amount
     * @param result the {@link BindingResult} object for validation results
     * @return the name of the home page view template
     */
    @PostMapping("/home/credit")
    public String creditBalance(Model model, @Valid BalanceDto balanceDto, BindingResult result) {
        logger.info("Processing POST /home/credit request");

        homeService.creditBalance(balanceDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Votre compte a bien été crédité");

        logger.info("Retrieving html home page");
        return "home";
    }

    /**
     * Handles the POST request to withdraw funds from the user's account balance.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @param balanceDto the {@link BalanceDto} object containing the withdrawal amount
     * @param result the {@link BindingResult} object for validation results
     * @return the name of the home page view template
     */
    @PostMapping("/home/withdraw")
    public String withdrawBalance(Model model, @Valid BalanceDto balanceDto, BindingResult result) {
        logger.info("Processing POST /home/withdraw request");

        homeService.withdrawBalance(balanceDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Votre compte a bien été débité");

        logger.info("Retrieving html home page");
        return "home";
    }
}
