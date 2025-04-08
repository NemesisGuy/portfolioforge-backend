package za.co.nemesisnet.portfolioforgebackend.security;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component // Mark as a Spring component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * This method is invoked when an unauthenticated user tries to access a protected resource.
     * We override it to return a 401 Unauthorized response instead of redirecting to a login page.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("Responding with unauthorized error. Message - {}", authException.getMessage());

        // Send 401 Unauthorized response
        // You could customize the response body further with a JSON error object if desired
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");

        // Example for JSON response (requires Jackson ObjectMapper to be available/injected):
        /*
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Assuming you have an ErrorResponse DTO or similar
        // String jsonPayload = new ObjectMapper().writeValueAsString(new ErrorResponse("Unauthorized", authException.getMessage()));
        // response.getOutputStream().println(jsonPayload);
        */
    }
}