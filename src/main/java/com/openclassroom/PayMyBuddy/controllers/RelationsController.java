package com.openclassroom.PayMyBuddy.controllers;

import com.openclassroom.PayMyBuddy.models.RelationDto;
import com.openclassroom.PayMyBuddy.models.UserProfileDto;
import com.openclassroom.PayMyBuddy.services.RelationsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * The {@code RelationsController} class handles operations related to managing user relationships.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>Displaying the user's relations</li>
 *   <li>Adding a new relation</li>
 * </ul>
 * </p>
 *
 * <p>It interacts with :
 * <ul>
 *     <li>{@link RelationsService} to fetch existing relations and add new ones</li>
 * </ul>
 * </p>
 */
@Controller
public class RelationsController {
    @Autowired
    private RelationsService relationsService;
    private static final Logger logger = LoggerFactory.getLogger(RelationsController.class);

    /**
     * Sets common attributes for the model to be used in the relations page view.
     *
     * @param model the {@link Model} object to which attributes are added
     */
    public void setAttributes(Model model) {
        List<UserProfileDto> relations = relationsService.getRelations();

        model.addAttribute(new RelationDto());
        model.addAttribute("relations", relations);
    }

    /**
     * Handles the GET request to display the relations page.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @return the name of the relations page view template
     */
    @GetMapping("/relations")
    public String getRelations(Model model) {
        logger.info("Processing GET /relations request");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving html relations page");
        return "relations";
    }

    /**
     * Handles the POST request to add a new relation.
     *
     * @param model the {@link Model} object to hold attributes for the view
     * @param relationDto the {@link RelationDto} containing the details of the new relation
     * @param result the {@link BindingResult} object to handle validation results
     * @return the name of the relations page view template
     */
    @PostMapping("/relations")
    public String postRelation(Model model, @Valid @ModelAttribute RelationDto relationDto, BindingResult result) {
        logger.info("Processing POST /relations request");

        relationsService.addRelation(relationDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Relation ajoutée");

        logger.info("Retrieving html relations page");
        return "relations";
    }
}
