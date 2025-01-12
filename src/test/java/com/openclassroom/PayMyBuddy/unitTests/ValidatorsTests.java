package com.openclassroom.PayMyBuddy.unitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import com.openclassroom.PayMyBuddy.services.Validators;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class ValidatorsTests {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RelationRepository relationRepository;
    @Mock
    private AppUserService appUserService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private Validators validators;

    @Test
    void testUsernameExists_Success() {
        when(appUserRepository.findByUsername(anyString())).thenReturn(new AppUser());

        boolean result = validators.usernameExists("test");

        assertTrue(result);
        verify(appUserRepository, times(1)).findByUsername("test");
    }

    @Test
    void testUsernameExists_Failure() {
        when(appUserRepository.findByUsername(anyString())).thenReturn(null);

        boolean result = validators.usernameExists("test");

        assertFalse(result);
        verify(appUserRepository, times(1)).findByUsername("test");
    }

    @Test
    void testEmailExists_Success() {
        when(appUserRepository.findByEmail(anyString())).thenReturn(new AppUser());

        boolean result = validators.emailExists("email@test.com");

        assertTrue(result);
        verify(appUserRepository, times(1)).findByEmail("email@test.com");
    }

    @Test
    void testEmailExists_Failure() {
        when(appUserRepository.findByEmail(anyString())).thenReturn(null);

        boolean result = validators.emailExists("email@test.com");

        assertFalse(result);
        verify(appUserRepository, times(1)).findByEmail("email@test.com");
    }

    @Test
    void testIsValidPassword_Success() {
        boolean result = validators.isValidPassword("Password1");

        assertTrue(result);
    }

    @Test
    void testIsValidPassword_Failure_TooShort() {
        boolean result = validators.isValidPassword("Pass1");

        assertFalse(result);
    }

    @Test
    void testIsValidPassword_Failure_NoUppercase() {
        boolean result = validators.isValidPassword("password1");

        assertFalse(result);
    }

    @Test
    void testIsValidPassword_Failure_NoLowercase() {
        boolean result = validators.isValidPassword("PASSWORD1");

        assertFalse(result);
    }

    @Test
    void testIsValidPassword_Failure_NoDigits() {
        boolean result = validators.isValidPassword("Password");

        assertFalse(result);
    }

    @Test
    void testPasswordMatches_Success() {
        boolean result = validators.passwordMatches("Password1", "Password1");

        assertTrue(result);
    }

    @Test
    void testPasswordMatches_Failure() {
        boolean result = validators.passwordMatches("Password1", "Password2");

        assertFalse(result);
    }

    @Test
    void testCheckPassword_Success() {
        AppUser appUser = new AppUser();
        appUser.setPassword("encodedPassword1");

        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        boolean result = validators.checkPassword("Password1");

        assertTrue(result);
    }

    @Test
    void testCheckPassword_Failure() {
        AppUser appUser = new AppUser();
        appUser.setPassword("encodedPassword1");

        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        boolean result = validators.checkPassword("Password0");

        assertFalse(result);
    }

    @Test
    void testRelationExists_Success() {
        when(relationRepository.findByUserAndRelatedUser(any(AppUser.class), any(AppUser.class))).thenReturn(
                new Relation());

        boolean result = validators.relationExists(new AppUser(), new AppUser());

        assertTrue(result);
    }

    @Test
    void testRelationExists_Failure() {
        when(relationRepository.findByUserAndRelatedUser(any(AppUser.class), any(AppUser.class))).thenReturn(null);

        boolean result = validators.relationExists(new AppUser(), new AppUser());

        assertFalse(result);
    }

    @Test
    void testIsBalanceNegative_success() {
        AppUser user = new AppUser();
        user.setBalance(BigDecimal.valueOf(20));

        when(appUserService.getConnectedUser()).thenReturn(user);

        boolean result = validators.isBalanceNegative(BigDecimal.valueOf(10));

        assertFalse(result);
    }

    @Test
    void testIsBalanceNegative_failure() {
        AppUser user = new AppUser();
        user.setBalance(BigDecimal.valueOf(20));

        when(appUserService.getConnectedUser()).thenReturn(user);

        boolean result = validators.isBalanceNegative(BigDecimal.valueOf(30));

        assertTrue(result);
    }
}
