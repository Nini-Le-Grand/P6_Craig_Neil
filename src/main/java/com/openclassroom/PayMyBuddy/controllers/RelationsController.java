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

@Controller
public class RelationsController {
    @Autowired
    private RelationsService relationsService;
    private static final Logger logger = LoggerFactory.getLogger(RelationsController.class);

    public void setAttributes(Model model) {
        List<UserProfileDto> relations = relationsService.getRelations();

        model.addAttribute(new RelationDto());
        model.addAttribute("relations", relations);
    }

    @GetMapping("/relations")
    public String getRelations(Model model) {
        logger.info("Processing GET /relations");

        logger.info("Adding model attributes");
        setAttributes(model);

        logger.info("Retrieving relations page");
        return "relations";
    }

    @PostMapping("/relations")
    public String postRelation(Model model, @Valid @ModelAttribute RelationDto relationDto, BindingResult result) {
        logger.info("Processing POST /relations");

        relationsService.addRelation(relationDto, result);

        logger.info("Adding model attributes");
        setAttributes(model);
        model.addAttribute("success", true);
        model.addAttribute("message", "Relation ajoutée");

        logger.info("Retrieving relations page");
        return "relations";
    }
}
