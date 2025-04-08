package za.co.nemesisnet.portfolioforgebackend.service; // Use your package

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Ensure correct entity/DTO paths if they differ

import za.co.nemesisnet.portfolioforgebackend.domain.Portfolio;
import za.co.nemesisnet.portfolioforgebackend.domain.User;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.PortfolioDTO;
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException; // Import custom exception
import za.co.nemesisnet.portfolioforgebackend.repository.PortfolioRepository;
import za.co.nemesisnet.portfolioforgebackend.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves the portfolio data for a specific user.
     * Converts the found Portfolio entity to a PortfolioDTO.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PortfolioDTO> getPortfolioByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .map(this::convertToDTO); // Convert found entity to DTO
    }

    /**
     * Retrieves portfolio data by its public slug.
     * Useful for public-facing views later.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PortfolioDTO> getPortfolioBySlug(String publicSlug) {
        return portfolioRepository.findByPublicSlug(publicSlug)
                .map(this::convertToDTO);
    }

    /**
     * Creates a new portfolio or updates an existing one for the specified user.
     * Handles checking for slug uniqueness against other users.
     */
    @Override
    @Transactional // Read-write transaction
    public PortfolioDTO createOrUpdatePortfolio(Long userId, PortfolioDTO portfolioDTO) {
        // 1. Find the User entity - throw ResourceNotFoundException if user doesn't exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));

        // 2. Check if the desired public slug is already taken by ANOTHER user (if slug is provided)
        if (portfolioDTO.getPublicSlug() != null && !portfolioDTO.getPublicSlug().isBlank()) {
            Optional<Portfolio> existingBySlug = portfolioRepository.findByPublicSlug(portfolioDTO.getPublicSlug());
            // Ensure the found slug doesn't belong to a DIFFERENT user
            if (existingBySlug.isPresent() && !existingBySlug.get().getUser().getId().equals(userId)) {
                // Use RuntimeException or a specific BusinessLogicException here
                throw new RuntimeException("Public Slug '" + portfolioDTO.getPublicSlug() + "' is already taken.");
            }
        }

        // 3. Find existing portfolio for the user or create a new one if not found
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Portfolio doesn't exist, create a new one
                    Portfolio newPortfolio = new Portfolio();
                    newPortfolio.setUser(user); // Associate with the found user
                    return newPortfolio;
                });

        // 4. Update the Portfolio entity fields from the incoming DTO data
        updateEntityFromDTO(portfolio, portfolioDTO);

        // 5. Save the entity (Hibernate handles insert vs update automatically)
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // 6. Convert the saved/updated entity back to DTO for the response
        return convertToDTO(savedPortfolio);
    }


    // --- Helper Methods for DTO <-> Entity Conversion ---
    // Consider using MapStruct or ModelMapper for more complex applications

    /**
     * Converts a Portfolio entity to a PortfolioDTO.
     * Excludes sensitive or unnecessary data like the User object itself.
     */
    private PortfolioDTO convertToDTO(Portfolio portfolio) {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setAboutMeText(portfolio.getAboutMeText());
        dto.setResumeUrl(portfolio.getResumeUrl());
        dto.setLinkedInUrl(portfolio.getLinkedInUrl());
        dto.setGithubUrl(portfolio.getGithubUrl());
        dto.setContactEmail(portfolio.getContactEmail());
        dto.setPublicSlug(portfolio.getPublicSlug());
        dto.setLastUpdatedAt(portfolio.getLastUpdatedAt());
        return dto;
    }

    /**
     * Updates fields of a Portfolio entity based on data from a PortfolioDTO.
     */
    private void updateEntityFromDTO(Portfolio portfolio, PortfolioDTO dto) {
        portfolio.setAboutMeText(dto.getAboutMeText());
        portfolio.setResumeUrl(dto.getResumeUrl());
        portfolio.setLinkedInUrl(dto.getLinkedInUrl());
        portfolio.setGithubUrl(dto.getGithubUrl());
        portfolio.setContactEmail(dto.getContactEmail());
        portfolio.setPublicSlug(dto.getPublicSlug());
        // lastUpdatedAt is handled automatically by @UpdateTimestamp
        // User association is set only during creation if needed
    }

    // No deletePortfolio method needed here due to cascading from User entity.
    /**
     * Finds the User ID associated with a given public portfolio slug.
     */
    @Override
    @Transactional(readOnly = true) // Read-only operation
    public Optional<Long> findUserIdBySlug(String publicSlug) {
        return portfolioRepository.findByPublicSlug(publicSlug) // Find portfolio by slug
                .map(Portfolio::getUser) // Map Optional<Portfolio> to Optional<User>
                .map(User::getId); // Map Optional<User> to Optional<Long> (user ID)
    }



}