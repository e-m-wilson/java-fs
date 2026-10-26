package com.spring.demo.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.spring.demo.domain.Role;
import com.spring.demo.dto.AuthResponseDTO;
import com.spring.demo.dto.AssignRoleRequestDTO;
import com.spring.demo.dto.PasswordChangeRequestDTO;
import com.spring.demo.dto.RegisterAndLoginRequestDTO;
import com.spring.demo.persistence.RoleRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// register + assignRole are the only methods that affect the DB
// so they are the only methods with @Transactional

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsManager userDetailsManager;
    private final RoleRepository roleRepository;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsManager userDetailsManager,
            RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsManager = userDetailsManager;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public AuthResponseDTO register(RegisterAndLoginRequestDTO request) {
        if (userDetailsManager.userExists(request.getUserName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }

        // this will seed our available roles when we first create a user
        // new users will always be ROLE_USER by default though
        if (roleRepository.findByName("ROLE_USER") == null) {
            roleRepository.save(new Role("ROLE_USER"));
        }
        if (roleRepository.findByName("ROLE_MANAGER") == null) {
            roleRepository.save(new Role("ROLE_MANAGER"));
        }
        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }

        UserDetails newUser = org.springframework.security.core.userdetails.User
                .withUsername(request.getUserName())
                .password(request.getPassword())
                .authorities("ROLE_USER")
                .build();
        userDetailsManager.createUser(newUser);
        return toResponse(newUser);
    }

     
    public AuthResponseDTO login(
            RegisterAndLoginRequestDTO request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUserName(), request.getPassword()));

        if (servletRequest.getSession(false) != null) {
            servletRequest.changeSessionId();
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return toResponse(authentication);
    }

   
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        logoutHandler.logout(request, response, authentication);
    }

    // this just takes a Authentication object from our login function
    // and transforms it to a response DTO so we can see the user and their 
    // roles in the response after logging in
    public AuthResponseDTO toResponse(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        return new AuthResponseDTO(authentication.getName(), roles);
    }

    // this just takes a UserDetails object from our register function
    // and transforms it to a response DTO so we can see the user and their 
    // roles in the response after registering 
    private AuthResponseDTO toResponse(UserDetails user) {
        List<String> roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        return new AuthResponseDTO(user.getUsername(), roles);
    }

    public void changePassword(PasswordChangeRequestDTO request) {
        userDetailsManager.changePassword(request.getOldPassword(), request.getNewPassword());
    }

    // lets us assign roles to users
    // not that we make sure it has the "ROLE_" prefix expected 
    // by spring security 
    @Transactional
    public AuthResponseDTO assignRole(AssignRoleRequestDTO request) {
        String roleName = request.getRole().trim().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        if (roleRepository.findByName(roleName) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown role: " + roleName);
        }

        UserDetails user;
        try {
            user = userDetailsManager.loadUserByUsername(request.getUserName());
        } catch (UsernameNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or inactive");
        }

        String[] authorities = Stream.concat(
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority),
                        Stream.of(roleName))
                .distinct()
                .toArray(String[]::new);
        UserDetails updatedUser = org.springframework.security.core.userdetails.User
                .withUserDetails(user)
                .authorities(authorities)
                .build();
        userDetailsManager.updateUser(updatedUser);
        return toResponse(updatedUser);
    }
}
