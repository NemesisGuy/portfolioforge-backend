package za.co.nemesisnet.portfolioforgebackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger; // Add logger
import org.slf4j.LoggerFactory; // Add logger
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // <<< Import Authentication
import org.springframework.web.bind.annotation.*;

import za.co.nemesisnet.portfolioforgebackend.domain.dto.PortfolioDTO;
import za.co.nemesisnet.portfolioforgebackend.service.PortfolioService;
import za.co.nemesisnet.portfolioforgebackend.service.UserDetailsImpl;

@RestController
@RequestMapping("/api/v1/me/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private static final Logger log = LoggerFactory.getLogger(PortfolioController.class); // Add logger

    /**
     * GET /api/v1/me/portfolio : Get the portfolio of the currently logged-in user.
     *
     * @param authentication The Authentication object representing the logged-in user.
     * @return 200 OK with PortfolioDTO if found, 404 Not Found otherwise, or error status.
     */
    @GetMapping
    public ResponseEntity<?> getMyPortfolio(Authentication authentication) { // <<< Inject Authentication

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("getMyPortfolio: Authentication object is null or not authenticated.");
            // Return 401 or 403, as authentication seems missing at controller level
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is required.");
        }

        Object principal = authentication.getPrincipal();
        log.debug("getMyPortfolio: Principal type: {}", (principal != null ? principal.getClass().getName() : "null"));

        if (!(principal instanceof UserDetailsImpl)) {
            log.error("getMyPortfolio: Principal is not an instance of UserDetailsImpl. Actual type: {}", (principal != null ? principal.getClass().getName() : "null"));
            // This indicates a configuration problem - internal server error might be appropriate
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing user details.");
        }

        // Cast is now safe
        UserDetailsImpl currentUser = (UserDetailsImpl) principal;
        log.info("getMyPortfolio: Processing request for user ID: {}", currentUser.getId());

        return portfolioService.getPortfolioByUserId(currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/me/portfolio : Create or update the portfolio of the currently logged-in user.
     *
     * @param authentication The Authentication object representing the logged-in user.
     * @param portfolioDTO The portfolio data from the request body.
     * @return 200 OK with the updated/created PortfolioDTO, or error status.
     */
    @PutMapping
    public ResponseEntity<?> updateMyPortfolio(
            Authentication authentication, // <<< Inject Authentication
            @Valid @RequestBody PortfolioDTO portfolioDTO) {

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("updateMyPortfolio: Authentication object is null or not authenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is required.");
        }

        Object principal = authentication.getPrincipal();
        log.debug("updateMyPortfolio: Principal type: {}", (principal != null ? principal.getClass().getName() : "null"));

        if (!(principal instanceof UserDetailsImpl)) {
            log.error("updateMyPortfolio: Principal is not an instance of UserDetailsImpl. Actual type: {}", (principal != null ? principal.getClass().getName() : "null"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing user details.");
        }

        // Cast is now safe
        UserDetailsImpl currentUser = (UserDetailsImpl) principal;
        log.info("updateMyPortfolio: Processing request for user ID: {}", currentUser.getId());


        try {
            PortfolioDTO savedPortfolio = portfolioService.createOrUpdatePortfolio(
                    currentUser.getId(),
                    portfolioDTO);
            return ResponseEntity.ok(savedPortfolio);
        } catch (RuntimeException e) {
            log.error("updateMyPortfolio: Error updating portfolio for user ID {}: {}", currentUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}