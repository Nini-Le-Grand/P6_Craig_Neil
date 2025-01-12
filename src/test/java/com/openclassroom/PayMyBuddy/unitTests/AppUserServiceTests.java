package com.openclassroom.PayMyBuddy.unitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTests {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AppUserService appUserService;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setId(1);
        appUser.setEmail("email@test.com");
        appUser.setUsername("test");
        appUser.setPassword("encodedPassword");
        appUser.setBalance(BigDecimal.valueOf(100.00));
    }

    @Test
    void testGetConnectedUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(appUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(appUserRepository.findById(anyInt())).thenReturn(appUser);

        AppUser result = appUserService.getConnectedUser();
        assertEquals(appUser, result);
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(appUserRepository.findByEmail(anyString())).thenReturn(appUser);

        UserDetails userDetails = appUserService.loadUserByUsername(appUser.getEmail());

        assertNotNull(userDetails);
        assertEquals(appUser.getUsername(), userDetails.getUsername());
        verify(appUserRepository, times(1)).findByEmail(appUser.getEmail());
    }

    @Test
    void testLoadUserByUsername_Failure_UserDoesNotExist() {
        when(appUserRepository.findByEmail(anyString())).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            appUserService.loadUserByUsername("email@test.com");
        });

        assertEquals("email@test.com" + " n'existe pas.", exception.getMessage());
        verify(appUserRepository, times(1)).findByEmail("email@test.com");
    }

    @Test
    void testUpdateUsername() {
        appUserService.updateUsername(appUser, "New");

        assertEquals("New", appUser.getUsername());
        verify(appUserRepository, times(1)).save(appUser);
    }

    @Test
    void testUpdateEmail() {
        appUserService.updateEmail(appUser, "newEmail@test.com");

        assertEquals("newEmail@test.com", appUser.getEmail());
        verify(appUserRepository, times(1)).save(appUser);
    }

    @Test
    void testUpdatePassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        appUserService.updatePassword(appUser, "password");

        assertEquals("encodedPassword", appUser.getPassword());
        verify(appUserRepository, times(1)).save(appUser);
    }

    @Test
    void testUpdateBalance() {
        appUserService.updateBalance(appUser, BigDecimal.valueOf(50.00));

        assertEquals(BigDecimal.valueOf(150.00), appUser.getBalance());
        verify(appUserRepository, times(1)).save(appUser);
    }
}

