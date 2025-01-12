package com.openclassroom.PayMyBuddy.services;

import com.openclassroom.PayMyBuddy.entities.AppUser;
import com.openclassroom.PayMyBuddy.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AppUserService implements UserDetailsService {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public AppUser getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        int id = ((AppUser) authentication.getPrincipal()).getId();
        return appUserRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email);
        if (appUser == null) {
            throw new UsernameNotFoundException(email + " n'existe pas.");
        }
        return appUser;
    }

    @Transactional
    public void updateUsername(AppUser appUser, String newUsername) {
        appUser.setUsername(newUsername);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void updateEmail(AppUser appUser, String newEmail) {
        appUser.setEmail(newEmail);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void updatePassword(AppUser appUser, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void updateBalance(AppUser appUser, BigDecimal amount) {
        BigDecimal newAmount = appUser.getBalance().add(amount);
        appUser.setBalance(newAmount);
        appUserRepository.save(appUser);
    }
}
