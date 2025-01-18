package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.exceptions.TransactionException;
import com.openclassroom.PayMyBuddy.models.TransactionDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing user transactions.
 */
@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    AppUserService appUserService;
    @Autowired
    Validators validators;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    /**
     * Retrieves the list of transactions for the connected user.
     *
     * <p>This method fetches all transactions for the current user and filters out transactions
     * where the user is the receiver, returning only the transactions they initiated.</p>
     *
     * @return a list of {@link Transaction} objects representing the user's transactions
     */
    public List<Transaction> getUserTransactions() {
        logger.info("Retrieving user transactions");
        int userId = appUserService.getConnectedUser().getId();
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        return transactions.stream()
                .filter(transaction -> transaction.getReceiver().getId() != userId)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new transaction based on the provided transaction data.
     *
     * <p>This method validates the input to ensure the receiver exists, checks that the user's
     * balance is not negative, and verifies that a relationship exists between the user and the receiver.</p>
     *
     * @param transactionDto the DTO containing information about the transaction to be added
     * @param result         the BindingResult object containing validation results
     * @throws TransactionException if there are validation errors or if an error occurs during the transaction process
     */
    public void addTransaction(TransactionDto transactionDto, BindingResult result) {
        logger.info("Processing addTransaction method");
        if (result.hasErrors()) {
            throw new TransactionException("Le formulaire contient une erreur");
        }

        if (!validators.emailExists(transactionDto.getReceiverEmail())) {
            throw new TransactionException("L'utilisateur n'existe pas");
        }

        if (validators.isBalanceNegative(transactionDto.getAmount())) {
            throw new TransactionException("Votre solde ne peut pas être négatif");
        }

        AppUser appUser = appUserService.getConnectedUser();
        AppUser receiver = appUserRepository.findByEmail(transactionDto.getReceiverEmail());

        if (!validators.relationExists(appUser, receiver)) {
            throw new TransactionException("La relation n'existe pas");
        }

        try {
            logger.info("Mapping new user transaction");
            BigDecimal amount = transactionDto.getAmount().setScale(2, RoundingMode.HALF_UP);
            Transaction transaction = new Transaction();
            transaction.setUser(appUser);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            transaction.setDescription(transactionDto.getDescription());

            transactionRepository.save(transaction);
            appUserService.updateBalance(appUser, amount.negate());
            appUserService.updateBalance(receiver, amount);
            logger.info("User transaction saved: {}", transaction);
        } catch (Exception e) {
            throw new TransactionException("Une erreur est survenue lors de la transaction");
        }
    }
}
