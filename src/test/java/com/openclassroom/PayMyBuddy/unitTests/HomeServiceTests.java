package com.openclassroom.PayMyBuddy.unitTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.exceptions.BalanceException;
import com.openclassroom.PayMyBuddy.models.BalanceDto;
import com.openclassroom.PayMyBuddy.repository.TransactionRepository;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import com.openclassroom.PayMyBuddy.services.HomeService;
import com.openclassroom.PayMyBuddy.services.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeServiceTests {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AppUserService appUserService;
    @Mock
    private Validators validators;
    @Mock
    private BindingResult bindingResult;
    @InjectMocks
    private HomeService homeService;
    private BalanceDto balanceDto;
    private AppUser appUser;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        balanceDto = new BalanceDto();
        balanceDto.setAmount(BigDecimal.valueOf(100)
                .setScale(2, RoundingMode.HALF_UP));

        appUser = new AppUser();
        appUser.setId(1);

        AppUser receiver = new AppUser();
        receiver.setId(2);

        Transaction transaction1 = new Transaction();
        transaction1.setUser(appUser);
        transaction1.setReceiver(appUser);
        transaction1.setAmount(BigDecimal.valueOf(10));

        Transaction transaction2 = new Transaction();
        transaction2.setUser(appUser);
        transaction2.setReceiver(receiver);
        transaction2.setAmount(BigDecimal.valueOf(20));

        transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
    }

    @Test
    void testGetUserOperation() {
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(transactionRepository.findDistinctByUserOrReceiver(appUser)).thenReturn(transactions);

        List<Transaction> result = homeService.getUserOperation();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(10), result.get(1)
                .getAmount());
        assertEquals(BigDecimal.valueOf(20)
                .negate(), result.get(0)
                .getAmount());
        verify(transactionRepository, times(1)).findDistinctByUserOrReceiver(appUser);
    }

    @Test
    void testCreditBalance_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);

        homeService.creditBalance(balanceDto, bindingResult);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(appUserService, times(1)).updateBalance(appUser, BigDecimal.valueOf(100)
                .setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCreditBalance_Failure_FormHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        BalanceException exception = assertThrows(BalanceException.class, () -> {
            homeService.creditBalance(balanceDto, bindingResult);
        });

        assertEquals("Vous devez renseigner un montant", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(appUserService, never()).updateBalance(any(AppUser.class), any());
    }

    @Test
    void testCreditBalance_Failure_AmountZero() {
        balanceDto.setAmount(BigDecimal.ZERO);

        when(bindingResult.hasErrors()).thenReturn(false);

        BalanceException exception = assertThrows(BalanceException.class, () -> {
            homeService.creditBalance(balanceDto, bindingResult);
        });

        assertEquals("Le montant doit être différend de 0", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(appUserService, never()).updateBalance(any(AppUser.class), any());
    }

    @Test
    void testCreditBalance_Failure_CatchBlock() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);

        doThrow(new RuntimeException("Database error")).when(transactionRepository)
                .save(any(Transaction.class));

        BalanceException exception = assertThrows(BalanceException.class, () -> {
            homeService.creditBalance(balanceDto, bindingResult);
        });

        assertEquals("Une erreur est survenue lors du crédit", exception.getMessage());
    }

    @Test
    void testWithdrawBalance_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);

        homeService.withdrawBalance(balanceDto, bindingResult);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(appUserService, times(1)).updateBalance(appUser, BigDecimal.valueOf(-100)
                .setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testWithdrawBalance_Failure_FormHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        BalanceException exception = assertThrows(BalanceException.class, () -> {
            homeService.withdrawBalance(balanceDto, bindingResult);
        });

        assertEquals("Vous devez renseigner un montant", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(appUserService, never()).updateBalance(any(AppUser.class), any());
    }

    @Test
    void testWithdrawBalance_Failure_AmountZero() {
        balanceDto.setAmount(BigDecimal.ZERO);

        when(bindingResult.hasErrors()).thenReturn(false);

        BalanceException exception = assertThrows(BalanceException.class, () -> {
            homeService.withdrawBalance(balanceDto, bindingResult);
        });

        assertEquals("Le montant doit être différend de 0", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(appUserService, never()).updateBalance(any(AppUser.class), any());
    }

    @Test
    void testWithdrawBalance_Failure_NegativeBalance() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.isBalanceNegative(any())).thenReturn(true);

        BalanceException exception = assertThrows(BalanceException.class, () -> {
            homeService.withdrawBalance(balanceDto, bindingResult);
        });

        assertEquals("Votre solde ne peut pas être négatif", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(appUserService, never()).updateBalance(any(AppUser.class), any());
    }

    @Test
    void testWithdrawBalance_Failure_CatchBlock() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);

        doThrow(new RuntimeException("Database error")).when(transactionRepository)
                .save(any(Transaction.class));

        BalanceException exception = assertThrows(BalanceException.class, () -> {
            homeService.withdrawBalance(balanceDto, bindingResult);
        });

        assertEquals("Une erreur est survenue lors du débit", exception.getMessage());
    }
}
