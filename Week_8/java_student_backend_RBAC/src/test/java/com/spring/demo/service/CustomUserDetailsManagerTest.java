package com.spring.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.spring.demo.domain.User;
import com.spring.demo.persistence.UserRepository;
import com.spring.demo.security.CustomUserDetailsManager;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsManagerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsManager manager;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUser_shouldPersistNewUser() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "alice",
                "password123",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userRepository.existsByUserName("alice")).thenReturn(false);

        manager.createUser(userDetails);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("alice", saved.getUserName());
        assertEquals("password123", saved.getPassword());
        assertTrue(saved.isEnabled());
    }

    @Test
    void userExists_shouldDelegateToRepository() {
        when(userRepository.existsByUserName("bob")).thenReturn(true);

        assertTrue(manager.userExists("bob"));
        when(userRepository.existsByUserName("missing")).thenReturn(false);

        assertFalse(manager.userExists("missing"));
    }

    @Test
    void updateUser_shouldReplaceExistingUserFields() {
        User existing = new User("charlie", "old-secret", true);
        when(userRepository.findByUserName("charlie")).thenReturn(existing);

        UserDetails updated = new org.springframework.security.core.userdetails.User(
                "charlie",
                "new-secret",
                false,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        manager.updateUser(updated);

        assertEquals("new-secret", existing.getPassword());
        assertFalse(existing.isEnabled());
        verify(userRepository).save(existing);
    }

    @Test
    void deleteUser_shouldDeleteMatchingUser() {
        User existing = new User("dana", "secret", true);
        when(userRepository.findByUserName("dana")).thenReturn(existing);

        manager.deleteUser("dana");

        verify(userRepository).delete(existing);
    }

    @Test
    void changePassword_shouldUpdateTheCurrentUserPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User currentUser = new User("erin", encoder.encode("old-password"), true);
        when(userRepository.findByUserName("erin")).thenReturn(currentUser);

        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "erin",
                "old-password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        manager.changePassword("old-password", "new-password");

        assertTrue(encoder.matches("new-password", currentUser.getPassword()));
        verify(userRepository).save(currentUser);
    }
}
