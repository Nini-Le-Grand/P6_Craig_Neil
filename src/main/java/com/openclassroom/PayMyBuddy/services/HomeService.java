package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.exceptions.BalanceException;
import com.openclassroom.PayMyBuddy.models.BalanceDto;
import com.openclassroom.PayMyBuddy.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AppUserService appUserService;
    @Autowired
    Validators validators;

    public List<Transaction> getUserOperation() {
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

    public void creditBalance(BalanceDto balanceDto, BindingResult result) {
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
            Transaction transaction = new Transaction();
            transaction.setUser(appUser);
            transaction.setReceiver(appUser);
            transaction.setAmount(amount);
            transaction.setDescription("Crédit");

            transactionRepository.save(transaction);
            appUserService.updateBalance(appUser, amount);
        } catch (Exception e) {
            throw new BalanceException("Une erreur est survenue lors du crédit");
        }
    }

    public void withdrawBalance(BalanceDto balanceDto, BindingResult result) {
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
            Transaction transaction = new Transaction();
            transaction.setUser(appUser);
            transaction.setReceiver(appUser);
            transaction.setAmount(amount.negate());
            transaction.setDescription("Débit");
            transactionRepository.save(transaction);

            appUserService.updateBalance(appUser, amount.negate());
        } catch (Exception e) {
            throw new BalanceException("Une erreur est survenue lors du débit");
        }
    }
}
