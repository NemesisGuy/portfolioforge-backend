package za.co.nemesisnet.portfolioforgebackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.nemesisnet.portfolioforgebackend.domain.Project;
import za.co.nemesisnet.portfolioforgebackend.domain.User;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.ProjectDTO;
// Create this custom exception later
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException;
import za.co.nemesisnet.portfolioforgebackend.repository.ProjectRepository;
import za.co.nemesisnet.portfolioforgebackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> findAllByUserId(Long userId) {
        List<Project> projects = projectRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId);
        return projects.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findByIdAndUserId(Long projectId, Long userId) {
        return projectRepository.findByIdAndUserId(projectId, userId)
                .map(this::convertToDto);
    }



    @Override
    @Transactional
    public ProjectDTO createProject(Long userId, ProjectDTO projectDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId)); // Use custom exception

        Project project = new Project();
        project.setUser(user); // Associate with the user
        updateEntityFromDto(project, projectDTO); // Map fields from DTO

        Project savedProject = projectRepository.save(project);
        return convertToDto(savedProject);
    }

    @Override
    @Transactional
    public Optional<ProjectDTO> updateProject(Long projectId, Long userId, ProjectDTO projectDTO) {
        // Find project ensuring ownership
        Optional<Project> existingProjectOpt = projectRepository.findByIdAndUserId(projectId, userId);

        if (existingProjectOpt.isPresent()) {
            Project projectToUpdate = existingProjectOpt.get();
            updateEntityFromDto(projectToUpdate, projectDTO); // Update fields
            Project updatedProject = projectRepository.save(projectToUpdate); // Save changes
            return Optional.of(convertToDto(updatedProject)); // Return updated DTO
        } else {
            return Optional.empty(); // Project not found or not owned by user
        }
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        // Find project ensuring ownership before deleting
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id " + projectId + " for user " + userId)); // More specific exception

        projectRepository.delete(project);
    }

    // --- Helper Mapping Methods ---
    private ProjectDTO convertToDto(Project project) {
        ProjectDTO dto = new ProjectDTO();
        // dto.setId(project.getId()); // Optionally include ID in response DTO
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setTechnologies(project.getTechnologies());
        dto.setImageUrl(project.getImageUrl());
        dto.setLiveUrl(project.getLiveUrl());
        dto.setRepoUrl(project.getRepoUrl());
        dto.setDisplayOrder(project.getDisplayOrder());
        // Optionally map timestamps (e.g., updatedAt)
        return dto;
    }

    private void updateEntityFromDto(Project project, ProjectDTO dto) {
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setTechnologies(dto.getTechnologies());
        project.setImageUrl(dto.getImageUrl());
        project.setLiveUrl(dto.getLiveUrl());
        project.setRepoUrl(dto.getRepoUrl());
        project.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0); // Handle potential null
        // User is set during creation or already exists on update
        // Timestamps are managed automatically
    }
}