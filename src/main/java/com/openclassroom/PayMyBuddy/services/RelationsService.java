package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.exceptions.RelationException;
import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import com.openclassroom.PayMyBuddy.models.UserProfileDto;
import com.openclassroom.PayMyBuddy.models.RelationDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Comparator;
import java.util.List;

@Service
public class RelationsService {
    @Autowired
    Validators validators;
    @Autowired
    AppUserService appUserService;
    @Autowired
    ProfileService profileService;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    RelationRepository relationRepository;

    public List<UserProfileDto> getRelations() {
        AppUser appUser = appUserService.getConnectedUser();

        List<Relation> relations = relationRepository.findAllByUserId(appUser.getId());
        List<AppUser> relationUsers = relations.stream().map(Relation::getRelatedUser).toList();

        return relationUsers.stream()
                .map(relationUser -> profileService.getProfile(relationUser))
                .sorted(Comparator.comparing(UserProfileDto::getUsername))
                .toList();
    }

    public void addRelation(RelationDto relationDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new RelationException("Le formulaire n'est pas renseigné correctement");
        }

        if (!validators.emailExists(relationDto.getEmail())) {
            throw new RelationException("L'utilisateur n'existe pas");
        }

        AppUser appUser = appUserService.getConnectedUser();
        AppUser relationUser = appUserRepository.findByEmail(relationDto.getEmail());

        if (validators.relationExists(appUser, relationUser)) {
            throw new RelationException("L'utilisateur a déjà été ajouté");
        }

        if (appUser.getId() == relationUser.getId()) {
            throw new RelationException("Vous ne pouvez pas vous ajouter");
        }

        try {
            Relation relation = new Relation();
            relation.setUser(appUser);
            relation.setRelatedUser(relationUser);
            relationRepository.save(relation);
        } catch (Exception e) {
            throw new RelationException("Une erreur s'est produite lors de la création de la relation");
        }
    }
}
