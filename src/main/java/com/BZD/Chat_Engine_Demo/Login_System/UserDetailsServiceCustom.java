package com.BZD.Chat_Engine_Demo.Login_System;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of UserDetailsService to load user-specific data.
 */
@Service
public class UserDetailsServiceCustom implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceCustom(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Create a UserDetails object without roles, assuming ROLE_USER for all authenticated users
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            true, true, true, true, // accountNonExpired, accountNonLocked, credentialsNonExpired, enabled
            AuthorityUtils.createAuthorityList("ROLE_USER") // Assign a default role for all users
        );
    }
}
