package com.openclassroom.PayMyBuddy.integrationTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.models.RegistrationDto;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AccessControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void testGetRegistrationPage() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"));
    }

    @Test
    void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRegister_Success() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("testuser@email.com");
        registrationDto.setPassword("Password1");
        registrationDto.setConfirmPassword("Password1");

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", registrationDto.getUsername())
                        .param("email", registrationDto.getEmail())
                        .param("password", registrationDto.getPassword())
                        .param("confirmPassword", registrationDto.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("success"))
                .andExpect(model().attribute("message", containsString("Utilisateur créé! Veuillez vous connecter")));

        AppUser user = appUserRepository.findByEmail("testuser@email.com");
        assert user != null;
        assert user.getUsername().equals("testuser");
        assert passwordEncoder.matches("Password1", user.getPassword());
    }

    @Test
    void testRegister_Failure() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("");
        registrationDto.setEmail("invalid-email");
        registrationDto.setPassword("pass");
        registrationDto.setConfirmPassword("different");

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", registrationDto.getUsername())
                        .param("email", registrationDto.getEmail())
                        .param("password", registrationDto.getPassword())
                        .param("confirmPassword", registrationDto.getConfirmPassword()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("message", "Le formulaire n'est pas renseigné correctement"));
    }

    @Test
    void testLogin_Success() throws Exception {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setEmail("testuser@email.com");
        user.setPassword(passwordEncoder.encode("Password1"));
        appUserRepository.save(user);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "testuser@email.com")
                        .param("password", "Password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void testLogin_Failure() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "error@email.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void testRedirection() throws Exception {
        mockMvc.perform(get("/home")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testLogout() throws Exception {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setEmail("testuser@email.com");
        user.setPassword(passwordEncoder.encode("Password1"));
        appUserRepository.save(user);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "testuser@email.com")
                        .param("password", "Password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        mockMvc.perform(get("/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"));
    }
}
