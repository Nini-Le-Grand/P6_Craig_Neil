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

/**
 * The {@code TransactionController} class manages user transactions within the application.
 *
 * <p>This controller supports:
 * <ul>
 *   <li>Displaying a list of the user's transactions</li>
 *   <li>Adding new transactions between users</li>
 * </ul>
 * </p>
 *
 * <p>It interacts with:
 * <ul>
 *   <li>{@link RelationsService} to fetch the user's relations</li>
 *   <li>{@link TransactionService} to handle transaction processing</li>
 * </ul>
 * </p>
 */
@Controller
public class TransactionController {
    @Autowired
    private RelationsService relationsService;
    @Autowired
    private TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    /**
     * Sets common attributes for the model to be used in the transaction page view.
     *
     * @param model the {@link Model} object to which attributes are added
     */
    public void setAttributes(Model model) {
        List<UserProfileDto> relations = relationsService.getRelations();
        List<Transaction> transactions = transactionService.getUserTransactions();

        model.addAttribute("relations", relations);
        model.addAttribute("transactions", transactions);
        model.addAttribute(new TransactionDto());
    }

    /**
     * Handles the GET request to display the transaction page.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @return the name of the transaction page view template
     */
    @GetMapping("/transaction")
    public String getTransaction(Model model) {
        logger.info("Processing GET /transaction request");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving html transaction page");
        return "transaction";
    }

    /**
     * Handles the POST request to add a new transaction.
     *
     * <p>The transaction details are validated before processing. If validation fails,
     * the errors are returned along with the updated transaction page.</p>
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @param transactionDto the {@link TransactionDto} containing details of the transaction
     * @param result the {@link BindingResult} object to handle validation results
     * @return the name of the transaction page view template
     */
    @PostMapping("/transaction")
    public String postTransaction(Model model, @Valid TransactionDto transactionDto, BindingResult result) {
        logger.info("Processing POST /transaction request");

        transactionService.addTransaction(transactionDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "La transaction a bien été effectuée");

        logger.info("Retrieving html transaction page");
        return "transaction";
    }
}
