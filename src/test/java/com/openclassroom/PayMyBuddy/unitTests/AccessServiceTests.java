package com.openclassroom.PayMyBuddy.unitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.exceptions.RegistrationException;
import com.openclassroom.PayMyBuddy.models.RegistrationDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.services.AccessService;
import com.openclassroom.PayMyBuddy.services.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class AccessServiceTests {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private Validators validators;
    @Mock
    private BindingResult result;
    @InjectMocks
    private AccessService accessService;
    private RegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationDto = new RegistrationDto();
        registrationDto.setUsername("testUser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("Password1");
        registrationDto.setConfirmPassword("Password1");
    }

    @Test
    void testRegister_Success() {
        when(result.hasErrors()).thenReturn(false);
        when(validators.usernameExists(registrationDto.getUsername())).thenReturn(false);
        when(validators.emailExists(registrationDto.getEmail())).thenReturn(false);
        when(validators.isValidPassword(registrationDto.getPassword())).thenReturn(true);
        when(validators.passwordMatches(registrationDto.getPassword(),
                registrationDto.getConfirmPassword())).thenReturn(true);

        accessService.register(registrationDto, result);

        verify(appUserRepository, times(1)).save(
                argThat(user -> "testUser".equals(user.getUsername()) && "test@example.com".equals(user.getEmail())));
    }

    @Test
    void testRegister_Failure_FormHasErrors() {
        when(result.hasErrors()).thenReturn(true);

        RegistrationException thrown = assertThrows(RegistrationException.class,
                () -> accessService.register(registrationDto, result));

        assertEquals("Le formulaire n'est pas renseigné correctement", thrown.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testRegister_Failure_UsernameAlreadyExists() {
        when(result.hasErrors()).thenReturn(false);
        when(validators.usernameExists(anyString())).thenReturn(true);

        RegistrationException thrown = assertThrows(RegistrationException.class,
                () -> accessService.register(registrationDto, result));

        assertEquals("Ce nom d'utilisateur est déjà utilisé", thrown.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testRegister_Failure_EmailAlreadyExists() {
        when(result.hasErrors()).thenReturn(false);
        when(validators.usernameExists(anyString())).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);

        RegistrationException thrown = assertThrows(RegistrationException.class,
                () -> accessService.register(registrationDto, result));

        assertEquals("Cet email est déjà utilisé", thrown.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testRegister_Failure_IncorrectPasswordPattern() {
        when(result.hasErrors()).thenReturn(false);
        when(validators.usernameExists(anyString())).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(false);
        when(validators.isValidPassword(anyString())).thenReturn(false);

        RegistrationException thrown = assertThrows(RegistrationException.class,
                () -> accessService.register(registrationDto, result));

        assertEquals("MDP : 6 char minimum : 1 MAJ, 1 min, 1 chiffre", thrown.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testRegister_Failure_PasswordsDoNotMatch() {
        when(result.hasErrors()).thenReturn(false);
        when(validators.usernameExists(anyString())).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(false);
        when(validators.isValidPassword(anyString())).thenReturn(true);
        when(validators.passwordMatches(anyString(), anyString())).thenReturn(false);

        RegistrationException thrown = assertThrows(RegistrationException.class,
                () -> accessService.register(registrationDto, result));

        assertEquals("Les mots de passe ne correspondent pas", thrown.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testRegister_Failure_ExceptionDuringSave() {
        when(result.hasErrors()).thenReturn(false);
        when(validators.usernameExists(anyString())).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(false);
        when(validators.isValidPassword(anyString())).thenReturn(true);
        when(validators.passwordMatches(anyString(), anyString())).thenReturn(true);

        doThrow(new RuntimeException()).when(appUserRepository)
                .save(any(AppUser.class));

        RegistrationException thrown = assertThrows(RegistrationException.class,
                () -> accessService.register(registrationDto, result));

        assertEquals("Une erreur est survenue lors de la création", thrown.getMessage());
    }
}
