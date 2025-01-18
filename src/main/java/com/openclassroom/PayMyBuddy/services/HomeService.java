package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.exceptions.BalanceException;
import com.openclassroom.PayMyBuddy.models.BalanceDto;
import com.openclassroom.PayMyBuddy.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling operations related to user balances and transactions.
 */
@Service
public class HomeService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AppUserService appUserService;
    @Autowired
    Validators validators;

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

    /**
     * Retrieves the list of transactions associated with the currently connected user.
     *
     * <p>This method fetches all transactions involving the user, adjusting the amounts to
     * reflect withdrawals as negative values, and returns them in reverse order.</p>
     *
     * @return a list of {@link Transaction} objects representing the user's operations
     */
    public List<Transaction> getUserOperation() {
        logger.info("Retrieving user operations");
        AppUser connectedUser = appUserService.getConnectedUser();
        List<Transaction> operations = transactionRepository.findDistinctByUserOrReceiver(connectedUser);
        List<Transaction> mappedOperations =
                operations.stream()
                        .map(operation -> {
                            if (!operation.getReceiver().equals(connectedUser)) {
                                operation.setAmount(operation.getAmount().negate());
                            }
                            return operation;
                        }).collect(Collectors.toList());
        Collections.reverse(mappedOperations);
        return mappedOperations;
    }

    /**
     * Credits a specified amount to the connected user's balance.
     *
     * <p>This method validates the input and creates a new transaction record for the credit.
     * It updates the user's balance accordingly.</p>
     *
     * @param balanceDto the DTO containing the amount to be credited
     * @param result    the BindingResult object containing validation results
     * @throws BalanceException if there are validation errors or if an error occurs during processing
     */
    public void creditBalance(BalanceDto balanceDto, BindingResult result) {
        logger.info("Processing creditBalance() method");
        BigDecimal amount = balanceDto.getAmount()
                .setScale(2, RoundingMode.HALF_UP);

        if (result.hasErrors()) {
            throw new BalanceException("Vous devez renseigner un montant");
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BalanceException("Le montant doit être différend de 0");
        }

        AppUser appUser = appUserService.getConnectedUser();

        try {
            logger.info("Mapping new transaction");
            Transaction transaction = new Transaction();
            transaction.setUser(appUser);
            transaction.setReceiver(appUser);
            transaction.setAmount(amount);
            transaction.setDescription("Crédit");

            transactionRepository.save(transaction);
            appUserService.updateBalance(appUser, amount);
            logger.info("User balance credited :{}", amount);
        } catch (Exception e) {
            throw new BalanceException("Une erreur est survenue lors du crédit");
        }
    }

    /**
     * Withdraws a specified amount from the connected user's balance.
     *
     * <p>This method validates the input and creates a new transaction record for the withdrawal.
     * It updates the user's balance accordingly, ensuring the balance does not go negative.</p>
     *
     * @param balanceDto the DTO containing the amount to be withdrawn
     * @param result    the BindingResult object containing validation results
     * @throws BalanceException if there are validation errors or if the balance is insufficient
     */
    public void withdrawBalance(BalanceDto balanceDto, BindingResult result) {
        logger.info("Processing withdrawBalance() method");
        BigDecimal amount = balanceDto.getAmount()
                .setScale(2, RoundingMode.HALF_UP);

        if (result.hasErrors()) {
            throw new BalanceException("Vous devez renseigner un montant");
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BalanceException("Le montant doit être différend de 0");
        }

        if (validators.isBalanceNegative(balanceDto.getAmount())) {
            throw new BalanceException("Votre solde ne peut pas être négatif");
        }

        AppUser appUser = appUserService.getConnectedUser();

        try {
            logger.info("Mapping new transaction");
            Transaction transaction = new Transaction();
            transaction.setUser(appUser);
            transaction.setReceiver(appUser);
            transaction.setAmount(amount.negate());
            transaction.setDescription("Débit");

            transactionRepository.save(transaction);
            appUserService.updateBalance(appUser, amount.negate());
            logger.info("User balance withdrawed :{}", amount);
        } catch (Exception e) {
            throw new BalanceException("Une erreur est survenue lors du débit");
        }
    }
}
