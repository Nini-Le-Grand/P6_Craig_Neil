package com.openclassroom.PayMyBuddy.integrationTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import com.openclassroom.PayMyBuddy.models.UserProfileDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class RelationsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setEmail("testuser@email.com");
        user.setPassword(passwordEncoder.encode("Password1"));
        appUserRepository.save(user);

        AppUser relationUser1 = new AppUser();
        relationUser1.setUsername("testrelation1");
        relationUser1.setEmail("testrelation1@email.com");
        relationUser1.setPassword(passwordEncoder.encode("Password2"));
        appUserRepository.save(relationUser1);

        AppUser relationUser2 = new AppUser();
        relationUser2.setUsername("testrelation2");
        relationUser2.setEmail("testrelation2@email.com");
        relationUser2.setPassword(passwordEncoder.encode("Password3"));
        appUserRepository.save(relationUser2);

        Relation relation = new Relation();
        relation.setUser(user);
        relation.setRelatedUser(relationUser1);
        relationRepository.save(relation);

        session = new MockHttpSession();

        mockMvc.perform(post("/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "testuser@email.com")
                .param("password", "Password1")
                .session(session));
    }

    @AfterEach
    void cleanUp() {
        relationRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    void testAccessingRelationsPage() throws Exception {
        mockMvc.perform(get("/relations")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().attribute("relations", hasSize(1)))
                .andDo(result -> {
                    List<UserProfileDto> relations = (List<UserProfileDto>) result.getModelAndView().getModel().get("relations");
                    assertThat(relations.get(0).getUsername(), is("testrelation1"));
                });
    }

    @Test
    void testPostRelation_Success() throws Exception {
        mockMvc.perform(post("/relations")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "testrelation2@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("message", "Relation ajoutée"))
                .andExpect(model().attribute("relations", hasSize(2)))
                .andDo(result -> {
                    List<UserProfileDto> relations = (List<UserProfileDto>) result.getModelAndView().getModel().get("relations");
                    assertThat(relations.get(0).getUsername(), is("testrelation1"));
                    assertThat(relations.get(1).getUsername(), is("testrelation2"));
                });
    }

    @Test
    void testPostRelation_Failure() throws Exception {
        mockMvc.perform(post("/relations")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "unknown@email.com"))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("message", "L'utilisateur n'existe pas"))
                .andExpect(model().attribute("relations", hasSize(1)));
    }
}
