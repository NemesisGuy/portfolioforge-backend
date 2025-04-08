package za.co.nemesisnet.portfolioforgebackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.SkillDTO;
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException; // Import custom exception
import za.co.nemesisnet.portfolioforgebackend.service.SkillService;
import za.co.nemesisnet.portfolioforgebackend.service.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/me/skills") // Base path for logged-in user's skills
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:5173") // Add later for frontend interaction
public class SkillController {

    private final SkillService skillService;
    private static final Logger log = LoggerFactory.getLogger(SkillController.class);

    /**
     * GET /api/v1/me/skills : Get all skills for the currently logged-in user.
     */
    @GetMapping
    public ResponseEntity<List<SkillDTO>> getMySkills(Authentication authentication) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Fetching skills for user ID: {}", currentUser.getId());

        List<SkillDTO> skills = skillService.findAllByUserId(currentUser.getId());
        return ResponseEntity.ok(skills);
    }

    /**
     * POST /api/v1/me/skills : Create a new skill for the logged-in user.
     */
    @PostMapping
    public ResponseEntity<?> createMySkill(Authentication authentication,
                                           @Valid @RequestBody SkillDTO skillDTO) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Creating skill for user ID: {}", currentUser.getId());

        try {
            SkillDTO createdSkill = skillService.createSkill(currentUser.getId(), skillDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
        } catch (RuntimeException e) {
            // Catch potential duplicate name exception from service
            log.warn("Failed to create skill for user ID {}: {}", currentUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); // Return 400 Bad Request
        }
    }

    /**
     * GET /api/v1/me/skills/{skillId} : Get a specific skill by ID for the logged-in user.
     */
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillDTO> getMySkillById(Authentication authentication,
                                                   @PathVariable Long skillId) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Fetching skill ID: {} for user ID: {}", skillId, currentUser.getId());

        return skillService.findByIdAndUserId(skillId, currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/me/skills/{skillId} : Update a specific skill by ID for the logged-in user.
     */
    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateMySkill(Authentication authentication,
                                           @PathVariable Long skillId,
                                           @Valid @RequestBody SkillDTO skillDTO) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Updating skill ID: {} for user ID: {}", skillId, currentUser.getId());

        try {
            Optional<SkillDTO> updatedSkill = skillService.updateSkill(skillId, currentUser.getId(), skillDTO);
            return updatedSkill
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) { // Catch specific not found during update attempt
            log.warn("Update failed: Skill ID {} not found for user ID {}", skillId, currentUser.getId());
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) { // Catch duplicate name during update
            log.warn("Update failed for skill ID {} for user ID {}: {}", skillId, currentUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); // Return 400 Bad Request
        }
    }

    /**
     * DELETE /api/v1/me/skills/{skillId} : Delete a specific skill by ID for the logged-in user.
     */
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteMySkill(Authentication authentication,
                                              @PathVariable Long skillId) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Deleting skill ID: {} for user ID: {}", skillId, currentUser.getId());

        try {
            skillService.deleteSkill(skillId, currentUser.getId());
            return ResponseEntity.noContent().build(); // 204 No Content on success
        } catch (ResourceNotFoundException e) {
            log.warn("Delete failed: Skill ID {} not found for user ID {}", skillId, currentUser.getId());
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            log.error("Error deleting skill ID {} for user ID {}", skillId, currentUser.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method to extract UserDetailsImpl safely
    private UserDetailsImpl getUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            log.warn("Could not extract UserDetailsImpl from Authentication object.");
            return null;
        }
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}