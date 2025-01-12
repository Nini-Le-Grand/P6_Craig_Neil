package com.openclassroom.PayMyBuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
