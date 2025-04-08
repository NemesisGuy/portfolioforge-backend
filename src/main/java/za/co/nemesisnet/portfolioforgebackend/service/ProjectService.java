package za.co.nemesisnet.portfolioforgebackend.service;





import za.co.nemesisnet.portfolioforgebackend.domain.dto.ProjectDTO;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    /**
     * Find all projects belonging to a specific user.
     * @param userId The ID of the user.
     * @return A list of ProjectDTOs, ordered for display.
     */
    List<ProjectDTO> findAllByUserId(Long userId);

    /**
     * Find a specific project by its ID, ensuring it belongs to the specified user.
     * @param projectId The ID of the project.
     * @param userId The ID of the user.
     * @return An Optional containing the ProjectDTO if found and owned by the user.
     */
    Optional<ProjectDTO> findByIdAndUserId(Long projectId, Long userId);

    /**
     * Create a new project for the specified user.
     * @param userId The ID of the user creating the project.
     * @param projectDTO The DTO containing the project details.
     * @return The created ProjectDTO.
     */
    ProjectDTO createProject(Long userId, ProjectDTO projectDTO);

    /**
     * Update an existing project, ensuring it belongs to the specified user.
     * @param projectId The ID of the project to update.
     * @param userId The ID of the user owning the project.
     * @param projectDTO The DTO containing the updated project details.
     * @return An Optional containing the updated ProjectDTO if found and updated successfully.
     */
    Optional<ProjectDTO> updateProject(Long projectId, Long userId, ProjectDTO projectDTO);

    /**
     * Delete a project, ensuring it belongs to the specified user.
     * @param projectId The ID of the project to delete.
     * @param userId The ID of the user owning the project.
     * @throws RuntimeException (or specific exception) if the project is not found or not owned by the user.
     */
    void deleteProject(Long projectId, Long userId);
}