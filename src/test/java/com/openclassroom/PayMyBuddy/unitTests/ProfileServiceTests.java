package com.openclassroom.PayMyBuddy.unitTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.exceptions.ProfileUpdateException;
import com.openclassroom.PayMyBuddy.models.UpdatePasswordDto;
import com.openclassroom.PayMyBuddy.models.UpdateProfileDto;
import com.openclassroom.PayMyBuddy.models.UserProfileDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import com.openclassroom.PayMyBuddy.services.ProfileService;
import com.openclassroom.PayMyBuddy.services.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTests {
    @Mock
    private Validators validators;
    @Mock
    private AppUserService appUserService;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BindingResult bindingResult;
    @InjectMocks
    private ProfileService profileService;
    private AppUser appUser;
    private UpdateProfileDto profileDto;
    private UpdatePasswordDto passwordDto;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("test");
        appUser.setEmail("test@email.com");

        profileDto = new UpdateProfileDto();
        profileDto.setUsername("newTest");
        profileDto.setEmail("newEmail@test.com");

        passwordDto = new UpdatePasswordDto();
        passwordDto.setOldPassword("Password0");
        passwordDto.setNewPassword("Password1");
        passwordDto.setConfirmPassword("Password1");
    }

    @Test
    void testGetProfile() {
        UserProfileDto profile = profileService.getProfile(appUser);

        assertNotNull(profile);
        assertEquals("test", profile.getUsername());
        assertEquals("test@email.com", profile.getEmail());
    }

    @Test
    void testUpdateProfile_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(validators.usernameExists(anyString())).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(false);

        profileService.updateProfile(profileDto, bindingResult);

        verify(appUserRepository, times(1)).save(any(AppUser.class));
        assertEquals("newTest", appUser.getUsername());
        assertEquals("newEmail@test.com", appUser.getEmail());
    }

    @Test
    void testUpdateProfile_Failure_FormHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updateProfile(profileDto, bindingResult);
        });

        assertEquals("Le formulaire n'est pas renseigné correctement", exception.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testUpdateProfile_Failure_UsernameAlreadyExists() {
        profileDto.setEmail("");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(validators.usernameExists(anyString())).thenReturn(true);

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updateProfile(profileDto, bindingResult);
        });

        assertEquals("Le username existe déjà", exception.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testUpdateProfile_Failure_EmailAlreadyExists() {
        profileDto.setUsername("");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(validators.emailExists(anyString())).thenReturn(true);

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updateProfile(profileDto, bindingResult);
        });

        assertEquals("L'email existe déjà", exception.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testUpdateProfile_Failure_CatchBlock() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(validators.usernameExists(anyString())).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(false);

        doThrow(new RuntimeException("Database error")).when(appUserRepository)
                .save(any());

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updateProfile(profileDto, bindingResult);
        });

        assertEquals("Une erreur s'est produite lors de la modification du profile", exception.getMessage());
    }

    @Test
    void testUpdatePassword_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.checkPassword(anyString())).thenReturn(true);
        when(validators.isValidPassword(anyString())).thenReturn(true);
        when(validators.passwordMatches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword1");
        when(appUserService.getConnectedUser()).thenReturn(appUser);

        assertDoesNotThrow(() -> profileService.updatePassword(passwordDto, bindingResult));

        verify(appUserRepository, times(1)).save(appUser);
        assertEquals("hashedPassword1", appUser.getPassword());
    }

    @Test
    void testUpdatePassword_Failure_FormHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updatePassword(passwordDto, bindingResult);
        });

        assertEquals("Le formulaire n'est pas renseigné correctement", exception.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testUpdatePassword_Failure_WrongPassword() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.checkPassword(anyString())).thenReturn(false);

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updatePassword(passwordDto, bindingResult);
        });

        assertEquals("le mot de passe n'est pas correct", exception.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testUpdatePassword_Failure_InvalidPasswordPattern() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.checkPassword(anyString())).thenReturn(true);
        when(validators.isValidPassword(anyString())).thenReturn(false);

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updatePassword(passwordDto, bindingResult);
        });

        assertEquals("MDP : 6 char minimum : 1 MAJ, 1 min, 1 chiffre", exception.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testUpdatePassword_Failure_PasswordMismatch() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.checkPassword(anyString())).thenReturn(true);
        when(validators.isValidPassword(anyString())).thenReturn(true);
        when(validators.passwordMatches(anyString(), anyString())).thenReturn(false);

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updatePassword(passwordDto, bindingResult);
        });

        assertEquals("Les mots de passe ne correspondent pas", exception.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testUpdatePassword_Failure_CatchBlock() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.checkPassword(anyString())).thenReturn(true);
        when(validators.isValidPassword(anyString())).thenReturn(true);
        when(validators.passwordMatches(anyString(), anyString())).thenReturn(true);

        doThrow(new RuntimeException("Database error")).when(passwordEncoder)
                .encode(any());

        ProfileUpdateException exception = assertThrows(ProfileUpdateException.class, () -> {
            profileService.updatePassword(passwordDto, bindingResult);
        });

        assertEquals("Une erreur s'est produite lors de la modification du profile", exception.getMessage());
    }
}