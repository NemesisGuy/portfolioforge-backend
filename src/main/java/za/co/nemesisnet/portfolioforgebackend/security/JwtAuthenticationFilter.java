package za.co.nemesisnet.portfolioforgebackend.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull; // Use Spring's NonNull for better static analysis hints
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Import UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils; // Utility for checking strings
import org.springframework.web.filter.OncePerRequestFilter; // Ensures filter runs only once per request

import java.io.IOException;

@Component // Mark as a Spring component so it can be picked up or injected
@RequiredArgsConstructor
// Inside JwtAuthenticationFilter.java

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger filterLogger = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // Use this logger

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        filterLogger.info("Processing request: {}", request.getRequestURI()); // Log request URI

        String jwt = null; // Initialize jwt to null
        try {
            jwt = getJwtFromRequest(request); // Get token first

            if (jwt != null) { // Log if token is found
                filterLogger.info("JWT Token found in request header.");

                if (tokenProvider.validateToken(jwt)) { // Check validation result
                    filterLogger.info("JWT Token is valid.");
                    String username = tokenProvider.getUsernameFromJwt(jwt);
                    filterLogger.info("Username extracted from token: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    filterLogger.info("UserDetails loaded for username: {}", username);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterLogger.info("Authentication set in SecurityContext for user: {}", username);

                } else {
                    filterLogger.warn("JWT Token validation failed."); // Log validation failure
                }
            } else {
                filterLogger.info("No JWT Token found in Authorization header."); // Log if token is missing
            }

        } catch (Exception ex) {
            filterLogger.error("Authentication error in JWT filter: {}", ex.getMessage(), ex); // Log full exception
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            filterLogger.debug("Authorization header found: {}", bearerToken); // Log found header
            return bearerToken.substring(7);
        }
        filterLogger.debug("No Bearer token found in Authorization header."); // Log missing Bearer
        return null;
    }
}
