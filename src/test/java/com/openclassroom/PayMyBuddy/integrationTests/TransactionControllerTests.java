package com.openclassroom.PayMyBuddy.integrationTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Relation;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import com.openclassroom.PayMyBuddy.repository.RelationRepository;
import com.openclassroom.PayMyBuddy.repository.TransactionRepository;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TransactionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private TransactionRepository transactionRepository;
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
        user = appUserRepository.findByEmail("testuser@email.com");
        user.setBalance(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        appUserRepository.save(user);

        AppUser relationUser = new AppUser();
        relationUser.setUsername("testrelation");
        relationUser.setEmail("testrelation@email.com");
        relationUser.setPassword(passwordEncoder.encode("Password2"));
        appUserRepository.save(relationUser);
        relationUser = appUserRepository.findByEmail("testrelation@email.com");
        relationUser.setBalance(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP));
        appUserRepository.save(relationUser);

        Relation relation = new Relation();
        relation.setUser(user);
        relation.setRelatedUser(relationUser);
        relationRepository.save(relation);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setReceiver(relationUser);
        transaction.setDescription("transaction1");
        transaction.setAmount(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP));
        transactionRepository.save(transaction);

        session = new MockHttpSession();

        mockMvc.perform(post("/login").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "testuser@email.com")
                        .param("password", "Password1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @AfterEach
    void cleanUp() {
        transactionRepository.deleteAll();
        relationRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    void testAccessingTransactionPage() throws Exception {
        mockMvc.perform(get("/transaction")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().attribute("relations", hasSize(1)))
                .andExpect(model().attribute("transactions", hasSize(1)))
                .andDo(result -> {
                    List<Transaction> transactions = (List<Transaction>) result.getModelAndView().getModel().get("transactions");
                    assertThat(transactions.get(0).getAmount(), is(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP)));
                });
    }

    @Test
    void testPostTransaction_Success() throws Exception {
        mockMvc.perform(post("/transaction")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("receiverEmail", "testrelation@email.com")
                        .param("description", "transaction2")
                        .param("amount", "50"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("message", "La transaction a bien été effectuée"))
                .andExpect(model().attribute("transactions", hasSize(2)))
                .andDo(result -> {
                    List<Transaction> transactions = (List<Transaction>) result.getModelAndView().getModel().get("transactions");
                    assertThat(transactions.get(1).getAmount(), is(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP)));
                });

        AppUser user = appUserRepository.findByEmail("testuser@email.com");
        assert user != null;
        assert user.getBalance().equals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP));

        AppUser relationUser = appUserRepository.findByEmail("testrelation@email.com");
        assert relationUser != null;
        assert relationUser.getBalance().equals(BigDecimal.valueOf(60).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testPostTransaction_Failure() throws Exception {
        mockMvc.perform(post("/transaction")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("receiverEmail", "testrelation1@email.com")
                        .param("description", "transaction2")
                        .param("amount", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("message", "L'utilisateur n'existe pas"))
                .andExpect(model().attribute("transactions", hasSize(1)));

        AppUser user = appUserRepository.findByEmail("testuser@email.com");
        assert user != null;
        assert user.getBalance().equals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
    }
}
