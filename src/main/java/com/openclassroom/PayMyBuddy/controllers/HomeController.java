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

@Controller
public class HomeController {
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private HomeService homeService;
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public void setAttributes(Model model) {
        AppUser appUser = appUserService.getConnectedUser();
        List<Transaction> operations = homeService.getUserOperation();

        model.addAttribute("username", appUser.getUsername());
        model.addAttribute("balance", appUser.getBalance());
        model.addAttribute("operations", operations);
        model.addAttribute(new BalanceDto());
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        logger.info("Processing GET /home");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving home page");
        return "home";
    }

    @PostMapping("/home/credit")
    public String creditBalance(Model model, @Valid BalanceDto balanceDto, BindingResult result) {
        logger.info("Processing POST /home/credit");

        homeService.creditBalance(balanceDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Votre compte a bien été crédité");

        logger.info("Retrieving home page");
        return "home";
    }

    @PostMapping("/home/withdraw")
    public String withdrawBalance(Model model, @Valid BalanceDto balanceDto, BindingResult result) {
        logger.info("Processing POST /home/withdraw");

        homeService.withdrawBalance(balanceDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Votre compte a bien été débité");

        logger.info("Retrieving home page");
        return "home";
    }
}
