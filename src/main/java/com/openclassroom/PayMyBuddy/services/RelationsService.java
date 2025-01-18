package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.exceptions.RelationException;
import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import com.openclassroom.PayMyBuddy.models.UserProfileDto;
import com.openclassroom.PayMyBuddy.models.RelationDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing user relations.
 */
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

    private static final Logger logger = LoggerFactory.getLogger(RelationsService.class);

    /**
     * Retrieves the list of relations for the connected user.
     *
     * <p>This method fetches all relations for the current user and maps them to
     * {@link UserProfileDto} objects containing profile information of each related user.
     * The list is sorted by username.</p>
     *
     * @return a sorted list of {@link UserProfileDto} representing the user's relations
     */
    public List<UserProfileDto> getRelations() {
        logger.info("Retrieving user relations");
        AppUser appUser = appUserService.getConnectedUser();

        List<Relation> relations = relationRepository.findAllByUserId(appUser.getId());
        List<AppUser> relationUsers = relations.stream().map(Relation::getRelatedUser).toList();

        return relationUsers.stream()
                .map(relationUser -> profileService.getProfile(relationUser))
                .sorted(Comparator.comparing(UserProfileDto::getUsername))
                .toList();
    }

    /**
     * Adds a new relation for the connected user based on the provided data.
     *
     * <p>This method validates the input to ensure the related user exists, checks that the
     * relation does not already exist, and prevents users from adding themselves as relations.</p>
     *
     * @param relationDto the DTO containing information about the relation to be added
     * @param result      the BindingResult object containing validation results
     * @throws RelationException if there are validation errors or if an error occurs during the addition process
     */
    public void addRelation(RelationDto relationDto, BindingResult result) {
        logger.info("Processing addRelation method");
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
            logger.info("Mapping new user relation");
            Relation relation = new Relation();
            relation.setUser(appUser);
            relation.setRelatedUser(relationUser);
            relationRepository.save(relation);
            logger.info("User relation saved: {}", relation);
        } catch (Exception e) {
            throw new RelationException("Une erreur s'est produite lors de la création de la relation");
        }
    }
}
