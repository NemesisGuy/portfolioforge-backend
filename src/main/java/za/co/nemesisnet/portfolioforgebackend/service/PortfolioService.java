package za.co.nemesisnet.portfolioforgebackend.service;



import za.co.nemesisnet.portfolioforgebackend.domain.dto.PortfolioDTO;

import java.util.Optional;

public interface PortfolioService {

    /**
     * Retrieves the portfolio data for a specific user.
     *
     * @param userId The ID of the user whose portfolio is requested.
     * @return An Optional containing the PortfolioDTO if the portfolio exists, otherwise empty.
     */
    Optional<PortfolioDTO> getPortfolioByUserId(Long userId);

    /**
     * Creates or updates the portfolio data for a specific user.
     * If the portfolio doesn't exist, it creates one. If it exists, it updates it.
     *
     * @param userId The ID of the user whose portfolio is being updated/created.
     * @param portfolioDTO The DTO containing the new portfolio data.
     * @return The updated or newly created PortfolioDTO.
     * @throws RuntimeException if the chosen publicSlug is already taken by another user.
     */
    PortfolioDTO createOrUpdatePortfolio(Long userId, PortfolioDTO portfolioDTO);

    /**
     * Retrieves the portfolio data by its public slug.
     * @param publicSlug The unique public identifier.
     * @return An Optional containing the PortfolioDTO if found.
     */
    Optional<PortfolioDTO> getPortfolioBySlug(String publicSlug); // For public viewing endpoint later


    /**
     * Finds the User ID associated with a given public portfolio slug.
     * Useful for retrieving related data like projects/skills based on the public slug.
     *
     * @param publicSlug The unique public identifier for the portfolio.
     * @return An Optional containing the User ID if a portfolio with the slug exists.
     */
    Optional<Long> findUserIdBySlug(String publicSlug);
}
