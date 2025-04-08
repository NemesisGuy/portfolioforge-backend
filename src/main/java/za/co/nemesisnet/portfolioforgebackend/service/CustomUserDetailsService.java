package za.co.nemesisnet.portfolioforgebackend.service; // Use your package

// Ensure your User entity is imported correctly (adjust if package name differs)
import za.co.nemesisnet.portfolioforgebackend.domain.User;
import za.co.nemesisnet.portfolioforgebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
// No longer need GrantedAuthority or SimpleGrantedAuthority here if UserDetailsImpl handles it
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Import the custom UserDetails implementation
//import za.co.nemesisnet.portfolioforgebackend.security.UserDetailsImpl; // <<< IMPORT THIS

// No longer need these if UserDetailsImpl handles authority mapping
// import java.util.Collection;
// import java.util.Collections;

@Service // Mark this as a Spring service bean
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user-specific data. Spring Security calls this method during authentication.
     * @param username The username identifying the user whose data is required.
     * @return a UserDetails object containing the user's information
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    @Transactional(readOnly = true) // Good practice for read-only operations
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try finding user by username OR email (allows login with either)
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found with username or email: " + username)));

        // --- CORRECTED RETURN STATEMENT ---
        // Return our custom UserDetailsImpl, passing the found User entity to its constructor.
        // UserDetailsImpl will handle getting username, password, authorities, etc., from the User entity.
        return new UserDetailsImpl(user); // <<< THIS IS THE CORRECT LINE

        // --- REMOVED INCORRECT RETURN ---
        /*
        // Convert our User entity's role(s) into Spring Security GrantedAuthority objects
        // For our simple String role:
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        // Return Spring Security's User object (or a custom UserDetails implementation)
        // This User object comes from org.springframework.security.core.userdetails.User
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),       // Username used by Spring Security
                user.getPassword(),       // Hashed password
                authorities               // User roles/permissions
        );
        */
    }
}