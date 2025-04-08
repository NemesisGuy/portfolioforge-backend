package za.co.nemesisnet.portfolioforgebackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.nemesisnet.portfolioforgebackend.domain.Skill;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Finds all Skills belonging to a specific user, ordered by category then name.
     * @param userId The ID of the user.
     * @return A list of skills.
     */
    List<Skill> findByUserIdOrderByCategoryAscNameAsc(Long userId);

    /**
     * Finds a specific Skill by its ID and the ID of its owning user.
     * @param id The ID of the skill.
     * @param userId The ID of the owning user.
     * @return An Optional containing the skill if found and owned by the user.
     */
    Optional<Skill> findByIdAndUserId(Long id, Long userId);

    /**
     * Checks if a specific skill exists for a given user.
     * @param id The skill ID.
     * @param userId The user ID.
     * @return true if the skill exists and belongs to the user, false otherwise.
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Optional: Find skill by name and user ID to prevent duplicates for the same user.
     * @param name Skill name.
     * @param userId User ID.
     * @return Optional containing the skill if found.
     */
    Optional<Skill> findByNameAndUserId(String name, Long userId);
}
