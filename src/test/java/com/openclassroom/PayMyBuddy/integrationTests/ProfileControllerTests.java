package com.openclassroom.PayMyBuddy.integrationTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ProfileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

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
        appUserRepository.deleteAll();
    }

    @Test
    void testAccessingProfilePage() throws Exception {
        mockMvc.perform(get("/profile")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("email", "testuser@email.com"));
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        mockMvc.perform(post("/profile")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "newusername")
                        .param("email", "newemail@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("username", "newusername"))
                .andExpect(model().attribute("email", "newemail@email.com"))
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("message", "Le profile a été mis à jour"));

        mockMvc.perform(get("/logout")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "newemail@email.com")
                        .param("password", "Password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void testUpdateProfile_Failure() throws Exception {
        mockMvc.perform(post("/profile")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("email", "newemail@email.com"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("email", "testuser@email.com"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("message", "Le username existe déjà"));
    }

    @Test
    void testUpdatePassword_Success() throws Exception {
        mockMvc.perform(post("/profile/password")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("oldPassword", "Password1")
                        .param("newPassword", "Password2")
                        .param("confirmPassword", "Password2"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("email", "testuser@email.com"))
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("message", "Le mot de passe a été mis à jour"));

        mockMvc.perform(get("/logout")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "testuser@email.com")
                        .param("password", "Password2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void testUpdatePassword_Failure() throws Exception {
        mockMvc.perform(post("/profile/password")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("oldPassword", "Password1")
                        .param("newPassword", "Password2")
                        .param("confirmPassword", "Password3"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("email", "testuser@email.com"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("message", "Les mots de passe ne correspondent pas"));
    }
}
