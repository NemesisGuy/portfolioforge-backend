package za.co.nemesisnet.portfolioforgebackend.service;



import za.co.nemesisnet.portfolioforgebackend.domain.dto.SkillDTO;

import java.util.List;
import java.util.Optional;

public interface SkillService {

    /**
     * Find all skills belonging to a specific user.
     * @param userId The ID of the user.
     * @return A list of SkillDTOs, ordered.
     */
    List<SkillDTO> findAllByUserId(Long userId);

    /**
     * Find a specific skill by its ID, ensuring it belongs to the specified user.
     * @param skillId The ID of the skill.
     * @param userId The ID of the user.
     * @return An Optional containing the SkillDTO if found and owned by the user.
     */
    Optional<SkillDTO> findByIdAndUserId(Long skillId, Long userId);

    /**
     * Create a new skill for the specified user.
     * @param userId The ID of the user creating the skill.
     * @param skillDTO The DTO containing the skill details.
     * @return The created SkillDTO.
     * @throws RuntimeException if a skill with the same name already exists for the user.
     */
    SkillDTO createSkill(Long userId, SkillDTO skillDTO);

    /**
     * Update an existing skill, ensuring it belongs to the specified user.
     * @param skillId The ID of the skill to update.
     * @param userId The ID of the user owning the skill.
     * @param skillDTO The DTO containing the updated skill details.
     * @return An Optional containing the updated SkillDTO if found and updated successfully.
     * @throws RuntimeException if trying to update to a name that already exists for the user (on a different skill).
     */
    Optional<SkillDTO> updateSkill(Long skillId, Long userId, SkillDTO skillDTO);

    /**
     * Delete a skill, ensuring it belongs to the specified user.
     * @param skillId The ID of the skill to delete.
     * @param userId The ID of the user owning the skill.
     */
    void deleteSkill(Long skillId, Long userId);
}
