package com.openclassroom.PayMyBuddy.unitTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.exceptions.TransactionException;
import com.openclassroom.PayMyBuddy.models.TransactionDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.TransactionRepository;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import com.openclassroom.PayMyBuddy.services.TransactionService;
import com.openclassroom.PayMyBuddy.services.Validators;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {
    @Mock
    private AppUserService appUserService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private Validators validators;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private BindingResult bindingResult;
    @InjectMocks
    private TransactionService transactionService;
    private AppUser appUser;
    private AppUser receiver;
    private TransactionDto transactionDto;
    private Transaction transaction1;
    private Transaction transaction2;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setId(1);
        appUser.setBalance(BigDecimal.valueOf(100));

        receiver = new AppUser();
        receiver.setId(2);
        receiver.setBalance(BigDecimal.valueOf(100));

        transactionDto = new TransactionDto();
        transactionDto.setAmount(BigDecimal.valueOf(20));
        transactionDto.setReceiverEmail("receiver@email.com");
        transactionDto.setDescription("Test");

        transaction1 = new Transaction();
        transaction1.setReceiver(appUser);
        transaction2 = new Transaction();
        transaction2.setReceiver(receiver);
        transactions = List.of(transaction1, transaction2);
    }

    @Test
    void testGetUserTransactions() {
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(transactionRepository.findAllByUserId(1)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions();

        assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(transaction2));
        Assertions.assertFalse(result.contains(transaction1));
    }

    @Test
    void testProcessTransaction_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(appUserRepository.findByEmail(anyString())).thenReturn(receiver);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(validators.relationExists(any(AppUser.class), any(AppUser.class))).thenReturn(true);

        transactionService.addTransaction(transactionDto, bindingResult);

        verify(transactionRepository, Mockito.times(1)).save(Mockito.any(Transaction.class));
        verify(appUserService, times(1)).updateBalance(appUser, BigDecimal.valueOf(20)
                .negate()
                .setScale(2, RoundingMode.HALF_UP));
        verify(appUserService, times(1)).updateBalance(receiver, BigDecimal.valueOf(20)
                .setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testProcessTransaction_Failure_FormHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.addTransaction(transactionDto, bindingResult);
        });

        assertEquals("Le formulaire contient une erreur", exception.getMessage());
    }

    @Test
    void testProcessTransaction_Failure_UserDoesNotExist() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(false);

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.addTransaction(transactionDto, bindingResult);
        });

        assertEquals("L'utilisateur n'existe pas", exception.getMessage());
    }

    @Test
    void testProcessTransaction_Failure_NegativeBalance() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(validators.isBalanceNegative(any())).thenReturn(true);

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.addTransaction(transactionDto, bindingResult);
        });

        assertEquals("Votre solde ne peut pas être négatif", exception.getMessage());
    }

    @Test
    void testProcessTransaction_relationDoesNotExist() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(validators.isBalanceNegative(any())).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(appUserRepository.findByEmail(anyString())).thenReturn(receiver);
        when(validators.relationExists(any(AppUser.class), any(AppUser.class))).thenReturn(false);

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.addTransaction(transactionDto, bindingResult);
        });

        assertEquals("La relation n'existe pas", exception.getMessage());
    }


    @Test
    void testProcessTransaction_Failure_CatchBlock() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(validators.isBalanceNegative(any())).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(appUserRepository.findByEmail(anyString())).thenReturn(receiver);
        when(validators.relationExists(any(AppUser.class), any(AppUser.class))).thenReturn(true);

        doThrow(new RuntimeException("Database error")).when(transactionRepository)
                .save(any());

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.addTransaction(transactionDto, bindingResult);
        });

        assertEquals("Une erreur est survenue lors de la transaction", exception.getMessage());
    }
}
