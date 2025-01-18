package com.openclassroom.PayMyBuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration class for the PayMyBuddy application.
 *
 * <p>This class is responsible for configuring the security settings of the application, including
 * authorization rules, login and logout behavior, and session management.</p>
 *
 * <p>It uses Spring Security to protect web resources and define security filters for incoming requests.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Configures the security filter chain for the application.
     *
     * @param http the HttpSecurity object to be configured
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during the configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/registration", "styles/access.css", "styles/fragments/info.css", "js/access.js")
                        .permitAll()
                        .anyRequest().hasRole("USER"))
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/home", true)
                        .permitAll())
                .logout(LogoutConfigurer -> LogoutConfigurer
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .permitAll())
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)
                        .expiredUrl("/login?sessionExpired=true"))
                .build();
    }

    /**
     * Defines a password encoder bean for encoding passwords.
     *
     * <p>This method uses BCrypt for password encoding to ensure secure storage of user passwords.</p>
     *
     * @return a PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
