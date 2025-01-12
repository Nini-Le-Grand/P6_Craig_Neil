package com.openclassroom.PayMyBuddy.controllers;

import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.models.TransactionDto;
import com.openclassroom.PayMyBuddy.models.UserProfileDto;
import com.openclassroom.PayMyBuddy.services.RelationsService;
import com.openclassroom.PayMyBuddy.services.TransactionService;
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
public class TransactionController {
    @Autowired
    private RelationsService relationsService;
    @Autowired
    private TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public void setAttributes(Model model) {
        List<UserProfileDto> relations = relationsService.getRelations();
        List<Transaction> transactions = transactionService.getUserTransactions();

        model.addAttribute("relations", relations);
        model.addAttribute("transactions", transactions);
        model.addAttribute(new TransactionDto());
    }

    @GetMapping("/transaction")
    public String getTransaction(Model model) {
        logger.info("Processing GET /transaction");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving transaction page");
        return "transaction";
    }

    @PostMapping("/transaction")
    public String postTransaction(Model model, @Valid TransactionDto transactionDto, BindingResult result) {
        logger.info("Processing POST /transaction");

        transactionService.processTransaction(transactionDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "La transaction a bien été effectuée");

        logger.info("Retrieving transaction page");
        return "transaction";
    }
}
