package com.openclassroom.PayMyBuddy.integrationTests;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.entities.Transaction;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

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

        AppUser relation = new AppUser();
        relation.setUsername("testrelation");
        relation.setEmail("testrelation@email.com");
        relation.setPassword(passwordEncoder.encode("Password2"));
        appUserRepository.save(relation);

        relation = appUserRepository.findByEmail("testrelation@email.com");
        relation.setBalance(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP));
        appUserRepository.save(relation);

        Transaction transaction1 = new Transaction();
        transaction1.setUser(user);
        transaction1.setReceiver(user);
        transaction1.setDescription("credit user");
        transaction1.setAmount(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setUser(relation);
        transaction2.setReceiver(relation);
        transaction2.setDescription("credit relation");
        transaction2.setAmount(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        transactionRepository.save(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setUser(user);
        transaction3.setReceiver(relation);
        transaction3.setDescription("transaction user to relation");
        transaction3.setAmount(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP));
        transactionRepository.save(transaction3);

        Transaction transaction4 = new Transaction();
        transaction4.setUser(relation);
        transaction4.setReceiver(user);
        transaction4.setDescription("Transaction relation to user");
        transaction4.setAmount(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP));
        transactionRepository.save(transaction4);

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
        appUserRepository.deleteAll();
    }

    @Test
    void testAccessingHomePage() throws Exception {
        mockMvc.perform(get("/home")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("balance", BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(model().attribute("operations", hasSize(3)))
                .andDo(result -> {
                    List<Transaction> operations = (List<Transaction>) result.getModelAndView().getModel().get("operations");
                    assertThat(operations.get(0).getAmount(), is(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP)));
                    assertThat(operations.get(1).getAmount(), is(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP).negate()));
                    assertThat(operations.get(2).getAmount(), is(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP)));
                });
    }

    @Test
    void testCreditBalance_Success() throws Exception {
        mockMvc.perform(post("/home/credit")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("balance", BigDecimal.valueOf(110).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("message", "Votre compte a bien été crédité"))
                .andExpect(model().attribute("operations", hasSize(4)))
                .andDo(result -> {
                    List<Transaction> operations = (List<Transaction>) result.getModelAndView().getModel().get("operations");
                    assertThat(operations.get(0).getAmount(), is(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP)));
                });
    }

    @Test
    void testCreditBalance_Failure() throws Exception {
        mockMvc.perform(post("/home/credit")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("balance", BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("message", "Vous devez renseigner un montant"))
                .andExpect(model().attribute("operations", hasSize(3)));
    }

    @Test
    void testWithdrawBalance_Success() throws Exception {
        mockMvc.perform(post("/home/withdraw")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", "30"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("balance", BigDecimal.valueOf(70).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("message", "Votre compte a bien été débité"))
                .andExpect(model().attribute("operations", hasSize(4)))
                .andDo(result -> {
                    List<Transaction> operations = (List<Transaction>) result.getModelAndView().getModel().get("operations");
                    assertThat(operations.get(0).getAmount(), is(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_UP).negate()));
                });
    }

    @Test
    void testWithdrawBalance_Failure() throws Exception {
        mockMvc.perform(post("/home/withdraw")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", "130"))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("balance", BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(model().attribute("operations", hasSize(3)))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("message", "Votre solde ne peut pas être négatif"));
    }
}
