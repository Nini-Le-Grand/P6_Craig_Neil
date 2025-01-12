package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.exceptions.TransactionException;
import com.openclassroom.PayMyBuddy.models.TransactionDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Transaction> getUserTransactions() {
        int userId = appUserService.getConnectedUser().getId();
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        return transactions.stream()
                .filter(transaction -> transaction.getReceiver().getId() != userId)
                .collect(Collectors.toList());
    }

    public void processTransaction(TransactionDto transactionDto, BindingResult result) {

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
            BigDecimal amount = transactionDto.getAmount().setScale(2, RoundingMode.HALF_UP);
            Transaction transaction = new Transaction();
            transaction.setUser(appUser);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            transaction.setDescription(transactionDto.getDescription());
            transactionRepository.save(transaction);

            appUserService.updateBalance(appUser, amount.negate());
            appUserService.updateBalance(receiver, amount);
        } catch (Exception e) {
            throw new TransactionException("Une erreur est survenue lors de la transaction");
        }
    }
}
