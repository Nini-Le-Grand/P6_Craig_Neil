package com.openclassroom.PayMyBuddy.unitTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import com.openclassroom.PayMyBuddy.exceptions.RelationException;
import com.openclassroom.PayMyBuddy.models.RelationDto;
import com.openclassroom.PayMyBuddy.models.UserProfileDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import com.openclassroom.PayMyBuddy.services.AppUserService;
import com.openclassroom.PayMyBuddy.services.ProfileService;
import com.openclassroom.PayMyBuddy.services.RelationsService;
import com.openclassroom.PayMyBuddy.services.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RelationsServiceTests {
    @Mock
    private Validators validators;
    @Mock
    private AppUserService appUserService;
    @Mock
    private ProfileService profileService;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RelationRepository relationRepository;
    @Mock
    private BindingResult bindingResult;
    @InjectMocks
    private RelationsService relationsService;
    private AppUser appUser;
    private AppUser relationUser1;
    private AppUser relationUser2;
    private List<Relation> relations;
    private UserProfileDto profile1;
    private UserProfileDto profile2;
    private Relation relation1;
    private RelationDto relationDto;

    @BeforeEach
    void setup() {
        appUser = new AppUser();
        appUser.setUsername("Neil");
        appUser.setEmail("neil@email.com");
        appUser.setId(1);

        relationUser1 = new AppUser();
        relationUser2 = new AppUser();
        relationUser1.setId(2);
        relationUser2.setId(3);

        relation1 = new Relation();
        relation1.setUser(appUser);
        relation1.setRelatedUser(relationUser1);
        Relation relation2 = new Relation();
        relation2.setUser(appUser);
        relation2.setRelatedUser(relationUser2);
        relations = List.of(relation1, relation2);

        profile1 = new UserProfileDto();
        profile1.setUsername("profile1");
        profile2 = new UserProfileDto();
        profile2.setUsername("profile2");

        relationDto = new RelationDto();
        relationDto.setEmail("email@test.com");
    }

    @Test
    void testGetRelations() {
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(relationRepository.findAllByUserId(1)).thenReturn(relations);
        when(profileService.getProfile(relationUser1)).thenReturn(profile1);
        when(profileService.getProfile(relationUser2)).thenReturn(profile2);

        List<UserProfileDto> result = relationsService.getRelations();

        assertEquals(2, result.size());
        assertEquals("profile1", result.get(0)
                .getUsername());
        assertEquals("profile2", result.get(1)
                .getUsername());
    }

    @Test
    void testGetRelations_EmptyRelations() {
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(relationRepository.findAllByUserId(1)).thenReturn(Collections.emptyList());

        List<UserProfileDto> result = relationsService.getRelations();

        assertTrue(result.isEmpty());
        verifyNoInteractions(profileService);
    }

    @Test
    void testAddRelation_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(appUserRepository.findByEmail(anyString())).thenReturn(relationUser1);
        when(validators.relationExists(any(AppUser.class), any(AppUser.class))).thenReturn(false);

        relationsService.addRelation(relationDto, bindingResult);
        verify(relationRepository, times(1)).save(relation1);
    }

    @Test
    void testAddRelation_Failure_FormHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        RelationException exception = assertThrows(RelationException.class, () -> {
            relationsService.addRelation(relationDto, bindingResult);
        });

        assertEquals("Le formulaire n'est pas renseigné correctement", exception.getMessage());
        verify(relationRepository, never()).save(any(Relation.class));
    }

    @Test
    void testAddRelation_Failure_EmailDoesNotExist() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(false);

        RelationException exception = assertThrows(RelationException.class, () -> {
            relationsService.addRelation(relationDto, bindingResult);
        });

        assertEquals("L'utilisateur n'existe pas", exception.getMessage());
        verify(relationRepository, never()).save(any(Relation.class));
    }

    @Test
    void testAddRelation_Failure_RelationAlreadyExists() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(appUserRepository.findByEmail(anyString())).thenReturn(relationUser1);
        when(validators.relationExists(any(AppUser.class), any(AppUser.class))).thenReturn(true);

        RelationException exception = assertThrows(RelationException.class, () -> {
            relationsService.addRelation(relationDto, bindingResult);
        });

        assertEquals("L'utilisateur a déjà été ajouté", exception.getMessage());
        verify(relationRepository, never()).save(any(Relation.class));
    }

    @Test
    void testAddRelation_Failure_SelfRelation() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(appUserRepository.findByEmail(anyString())).thenReturn(appUser);
        when(validators.relationExists(any(AppUser.class), any(AppUser.class))).thenReturn(false);

        RelationException exception = assertThrows(RelationException.class, () -> {
            relationsService.addRelation(relationDto, bindingResult);
        });

        assertEquals("Vous ne pouvez pas vous ajouter", exception.getMessage());
        verify(relationRepository, never()).save(any(Relation.class));
    }

    @Test
    void testAddRelation_Failure_CatchBlock() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validators.emailExists(anyString())).thenReturn(true);
        when(appUserService.getConnectedUser()).thenReturn(appUser);
        when(appUserRepository.findByEmail(anyString())).thenReturn(relationUser1);
        when(validators.relationExists(any(AppUser.class), any(AppUser.class))).thenReturn(false);

        doThrow(new RuntimeException()).when(relationRepository)
                .save(any(Relation.class));

        RelationException exception = assertThrows(RelationException.class, () -> {
            relationsService.addRelation(relationDto, bindingResult);
        });

        assertEquals("Une erreur s'est produite lors de la création de la relation", exception.getMessage());
    }
}
