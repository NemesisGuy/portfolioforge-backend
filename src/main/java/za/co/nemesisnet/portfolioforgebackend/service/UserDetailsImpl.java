package za.co.nemesisnet.portfolioforgebackend.service;

import lombok.Getter; // Use Lombok Getter
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.nemesisnet.portfolioforgebackend.domain.User;


import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user; // Store the original User entity

    @Getter // Lombok getter for ID
    private final Long id; // Store ID for convenience

    public UserDetailsImpl(User user) {
        this.user = user;
        this.id = user.getId(); // Set ID from user
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Use the role from the wrapped User entity
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Get password from User entity
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Get username from User entity
    }

    // --- Account Status Flags (delegate to User entity if fields exist, otherwise return true) ---
    @Override
    public boolean isAccountNonExpired() {
        return true; // Assuming accounts don't expire by default
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Assuming accounts are not locked by default
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Assuming credentials don't expire by default
    }

    @Override
    public boolean isEnabled() {
        return true; // Assuming accounts are enabled by default
    }
}
