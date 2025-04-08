package za.co.nemesisnet.portfolioforgebackend.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.nemesisnet.portfolioforgebackend.domain.Portfolio;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /**
     * Finds the Portfolio associated with a specific User ID.
     * @param userId The ID of the User.
     * @return An Optional containing the Portfolio if found.
     */
    Optional<Portfolio> findByUserId(Long userId);

    // Alternative:
    // Optional<Portfolio> findByUser(User user);

    /**
     * Checks if a portfolio exists for a given user ID.
     * @param userId the user ID
     * @return true if a portfolio exists, false otherwise
     */
    boolean existsByUserId(Long userId);

    /**
     * Finds a portfolio by its public slug (if you implement slugs)
     * @param publicSlug The unique public identifier.
     * @return An Optional containing the Portfolio if found.
     */
    Optional<Portfolio> findByPublicSlug(String publicSlug); // Add if using slugs publicly

    /**
     * Checks if a portfolio exists with the given public slug.
     * @param publicSlug The slug to check.
     * @return true if exists, false otherwise.
     */
    boolean existsByPublicSlug(String publicSlug); // Add if using slugs publicly
}