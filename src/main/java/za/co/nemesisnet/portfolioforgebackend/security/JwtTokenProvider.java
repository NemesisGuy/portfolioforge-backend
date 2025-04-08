package za.co.nemesisnet.portfolioforgebackend.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // Mark as a Spring component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${portfolioforge.jwt.secret}") // Inject secret from application.properties
    private String jwtSecret;

    @Value("${portfolioforge.jwt.expiration-ms}") // Inject expiration time
    private int jwtExpirationMs;

    private SecretKey key() {
        // Generate SecretKey from the base64 encoded secret string
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Generate JWT token from Authentication object
    public String generateToken(Authentication authentication) {
        // Get the username from the authenticated principal
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        // Or if principal is your User entity: String username = ((User) authentication.getPrincipal()).getUsername();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Build the JWT token
        return Jwts.builder()
                .subject(username) // Set username as the subject
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key(), Jwts.SIG.HS512) // Sign with HS512 algorithm and the secret key
                .compact();
    }

    // Get username from JWT token
    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key()) // Verify using the secret key
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Get the payload/claims part

        return claims.getSubject(); // Extract the username (subject)
    }

    // Validate JWT token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key()) // Verify using the secret key
                    .build()
                    .parseSignedClaims(authToken); // Parse and validate signature/expiration
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
