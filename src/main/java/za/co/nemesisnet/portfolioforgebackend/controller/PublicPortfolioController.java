package za.co.nemesisnet.portfolioforgebackend.controller;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.co.nemesisnet.portfolioforgebackend.domain.dto.PortfolioDTO;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.ProjectDTO;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.SkillDTO;
import za.co.nemesisnet.portfolioforgebackend.service.PortfolioService;
import za.co.nemesisnet.portfolioforgebackend.service.ProjectService;
import za.co.nemesisnet.portfolioforgebackend.service.SkillService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/portfolios") // Base path for public portfolio viewing
@RequiredArgsConstructor
// @CrossOrigin(origins = "*") // Allow all origins for public endpoints, or configure specific ones
public class PublicPortfolioController {

    private final PortfolioService portfolioService;
    private final ProjectService projectService;
    private final SkillService skillService;
    private static final Logger log = LoggerFactory.getLogger(PublicPortfolioController.class);

    /**
     * GET /api/v1/portfolios/{slugOrUsername} : Get public portfolio details by slug or username.
     */
    @GetMapping("/{slugOrUsername}")
    public ResponseEntity<PortfolioDTO> getPublicPortfolio(@PathVariable String slugOrUsername) {
        log.info("Request received for public portfolio with identifier: {}", slugOrUsername);
        // Try finding by slug first (primary identifier)
        Optional<PortfolioDTO> portfolioOpt = portfolioService.getPortfolioBySlug(slugOrUsername);

        // TODO Optional: Add fallback to lookup by username if slug not found
        // if (portfolioOpt.isEmpty()) {
        //    // logic to find user by username, then get their portfolio by userId
        // }

        return portfolioOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/portfolios/{slugOrUsername}/projects : Get public projects list by slug or username.
     */
    @GetMapping("/{slugOrUsername}/projects")
    public ResponseEntity<List<ProjectDTO>> getPublicProjects(@PathVariable String slugOrUsername) {
        log.info("Request received for public projects with identifier: {}", slugOrUsername);

        // Find the userId associated with the slug/username
        Optional<Long> userIdOpt = portfolioService.findUserIdBySlug(slugOrUsername);
        // TODO Optional: Add fallback to lookup by username if slug not found

        if (userIdOpt.isPresent()) {
            List<ProjectDTO> projects = projectService.findAllByUserId(userIdOpt.get());
            return ResponseEntity.ok(projects);
        } else {
            // If user ID not found based on slug, return 404
            return ResponseEntity.notFound().build();
            // Or return an empty list: return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * GET /api/v1/portfolios/{slugOrUsername}/skills : Get public skills list by slug or username.
     */
    @GetMapping("/{slugOrUsername}/skills")
    public ResponseEntity<List<SkillDTO>> getPublicSkills(@PathVariable String slugOrUsername) {
        log.info("Request received for public skills with identifier: {}", slugOrUsername);

        // Find the userId associated with the slug/username
        Optional<Long> userIdOpt = portfolioService.findUserIdBySlug(slugOrUsername);
        // TODO Optional: Add fallback to lookup by username if slug not found

        if (userIdOpt.isPresent()) {
            List<SkillDTO> skills = skillService.findAllByUserId(userIdOpt.get());
            return ResponseEntity.ok(skills);
        } else {
            return ResponseEntity.notFound().build();
            // Or return an empty list: return ResponseEntity.ok(Collections.emptyList());
        }
    }
}