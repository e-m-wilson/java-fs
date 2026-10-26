package com.spring.demo.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.spring.demo.domain.Role;
import com.spring.demo.domain.User;
import com.spring.demo.persistence.RoleRepository;
import com.spring.demo.persistence.UserRepository;

@Service
public class CustomUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomUserDetailsManager(UserRepository userRepo, RoleRepository roleRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(UserDetails user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        String username = user.getUsername();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        // make sure this is enforced at db level, we don't want race conditions
        if (userExists(username)) {
            throw new IllegalArgumentException("User already exists: " + username);
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        User newUser = new User();
        newUser.setUserName(username);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEnabled(user.isEnabled());
        newUser.setRoles(resolveRoles(user.getAuthorities()));
        userRepo.save(newUser);
    }

    @Override
    public void updateUser(UserDetails user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        String username = user.getUsername();
        User existingUser = userRepo.findByUserName(username);
        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        // we have to give the password some thought here
        // we don't want to encode an already encoded password
        // we should probably just use the specific method reserved for 
        // password changes, so this method should ultimately only change
        // the enabled status and roles (or any other future fields on user we may want)
        // we should make sure the old password is preserved as-is
        //existingUser.setPassword(user.getPassword());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setRoles(resolveRoles(user.getAuthorities()));
        userRepo.save(existingUser);
    }

    @Override
    public void deleteUser(String username) {
        User existingUser = userRepo.findByUserName(username);
        if (existingUser != null) {
            userRepo.delete(existingUser);
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No authenticated user available to change password");
        }

        User user = userRepo.findByUserName(authentication.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + authentication.getName());
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return userRepo.existsByUserName(username);
    }

    // this method comes from UserDetailsService interface
    // UserDetailsService interface is inherited by UserDetailsManager
    // we use this method for authentication, most of the other methods are for
    // interacting with user records and are required overrides from UserDetailsManager interface
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepo.findByUserNameAndEnabledTrue(userName);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password, or user is inactive.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    private Set<Role> resolveRoles(
        Collection<? extends GrantedAuthority> authorities) {

        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .map(roleName -> {
                Role role = roleRepo.findByName(roleName);

                if (role == null) {
                    throw new IllegalArgumentException(
                        "Unknown role: " + roleName
                    );
                }

                return role;
            })
            .collect(Collectors.toSet());
    }
}
