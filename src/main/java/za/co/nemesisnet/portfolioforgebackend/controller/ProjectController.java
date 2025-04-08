package za.co.nemesisnet.portfolioforgebackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import za.co.nemesisnet.portfolioforgebackend.domain.dto.ProjectDTO;
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException;
import za.co.nemesisnet.portfolioforgebackend.service.ProjectService;
import za.co.nemesisnet.portfolioforgebackend.service.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/me/projects") // Base path for logged-in user's projects
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:5173") // Add later for frontend interaction
public class ProjectController {

    private final ProjectService projectService;
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    /**
     * GET /api/v1/me/projects : Get all projects for the currently logged-in user.
     */
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getMyProjects(Authentication authentication) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Fetching projects for user ID: {}", currentUser.getId());

        List<ProjectDTO> projects = projectService.findAllByUserId(currentUser.getId());
        return ResponseEntity.ok(projects);
    }

    /**
     * POST /api/v1/me/projects : Create a new project for the logged-in user.
     */
    @PostMapping
    public ResponseEntity<ProjectDTO> createMyProject(Authentication authentication,
                                                      @Valid @RequestBody ProjectDTO projectDTO) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Creating project for user ID: {}", currentUser.getId());

        ProjectDTO createdProject = projectService.createProject(currentUser.getId(), projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * GET /api/v1/me/projects/{projectId} : Get a specific project by ID for the logged-in user.
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getMyProjectById(Authentication authentication,
                                                       @PathVariable Long projectId) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Fetching project ID: {} for user ID: {}", projectId, currentUser.getId());

        return projectService.findByIdAndUserId(projectId, currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/me/projects/{projectId} : Update a specific project by ID for the logged-in user.
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateMyProject(Authentication authentication,
                                                      @PathVariable Long projectId,
                                                      @Valid @RequestBody ProjectDTO projectDTO) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Updating project ID: {} for user ID: {}", projectId, currentUser.getId());

        Optional<ProjectDTO> updatedProject = projectService.updateProject(projectId, currentUser.getId(), projectDTO);

        return updatedProject
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Or handle error differently if needed
    }

    /**
     * DELETE /api/v1/me/projects/{projectId} : Delete a specific project by ID for the logged-in user.
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteMyProject(Authentication authentication,
                                                @PathVariable Long projectId) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Deleting project ID: {} for user ID: {}", projectId, currentUser.getId());

        try {
            projectService.deleteProject(projectId, currentUser.getId());
            return ResponseEntity.noContent().build(); // 204 No Content on success
        } catch (ResourceNotFoundException e) { // Catch specific exception if thrown by service
            log.warn("Delete failed: Project ID {} not found for user ID {}", projectId, currentUser.getId());
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) { // Catch unexpected errors
            log.error("Error deleting project ID {} for user ID {}", projectId, currentUser.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
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