package za.co.nemesisnet.portfolioforgebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // Keep if needed
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Keep if needed
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // Import for Session Policy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Import this filter class
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import za.co.nemesisnet.portfolioforgebackend.security.JwtAuthenticationEntryPoint; // Import EntryPoint (Create next)
import za.co.nemesisnet.portfolioforgebackend.security.JwtAuthenticationFilter; // Import our filter
import lombok.RequiredArgsConstructor; // If using constructor injection for filter/entrypoint

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Add if injecting filter/entryPoint via constructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // --- Explicitly define a stateless SecurityContextRepository ---
    // This reinforces that we don't want sessions storing security context.
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new NullSecurityContextRepository();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception { // Inject the repository
        http
                .csrf(AbstractHttpConfigurer::disable)

                // --- Explicitly set the stateless SecurityContextRepository ---
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository) // Use the NullSecurityContextRepository
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )

                // --- Session Management (Redundant with NullSecurityContextRepository but doesn't hurt) ---
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/portfolios/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Secure endpoints
                        .requestMatchers("/api/v1/auth/all").authenticated()
                        // Secure all other endpoints
                        .anyRequest().authenticated()
                )

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
