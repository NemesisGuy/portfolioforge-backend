package za.co.nemesisnet.portfolioforgebackend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.co.nemesisnet.portfolioforgebackend.domain.Skill;
import za.co.nemesisnet.portfolioforgebackend.domain.User;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.SkillDTO;
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException;
import za.co.nemesisnet.portfolioforgebackend.repository.SkillRepository;
import za.co.nemesisnet.portfolioforgebackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> findAllByUserId(Long userId) {
        List<Skill> skills = skillRepository.findByUserIdOrderByCategoryAscNameAsc(userId);
        return skills.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SkillDTO> findByIdAndUserId(Long skillId, Long userId) {
        return skillRepository.findByIdAndUserId(skillId, userId).map(this::convertToDto);
    }

    @Override
    @Transactional
    public SkillDTO createSkill(Long userId, SkillDTO skillDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Optional: Check if skill name already exists for this user
        skillRepository.findByNameAndUserId(skillDTO.getName(), userId).ifPresent(s -> {
            throw new RuntimeException("Skill with name '" + skillDTO.getName() + "' already exists for this user.");
        });

        Skill skill = new Skill();
        skill.setUser(user);
        updateEntityFromDto(skill, skillDTO); // Use helper to map fields

        Skill savedSkill = skillRepository.save(skill);
        return convertToDto(savedSkill);
    }

    @Override
    @Transactional
    public Optional<SkillDTO> updateSkill(Long skillId, Long userId, SkillDTO skillDTO) {
        // Find the existing skill ensuring ownership
        Skill skillToUpdate = skillRepository.findByIdAndUserId(skillId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Skill not found with id " + skillId + " for user " + userId));

        // Optional: Check if the new name conflicts with another existing skill for the same user
        if (!skillToUpdate.getName().equalsIgnoreCase(skillDTO.getName())) { // Check only if name changed
            skillRepository.findByNameAndUserId(skillDTO.getName(), userId).ifPresent(s -> {
                // Check if the found skill is different from the one being updated
                if (!s.getId().equals(skillId)) {
                    throw new RuntimeException("Skill with name '" + skillDTO.getName() + "' already exists for this user.");
                }
            });
        }

        updateEntityFromDto(skillToUpdate, skillDTO); // Update fields
        Skill updatedSkill = skillRepository.save(skillToUpdate);
        return Optional.of(convertToDto(updatedSkill));
    }

    @Override
    @Transactional
    public void deleteSkill(Long skillId, Long userId) {
        // Check existence and ownership before deleting
        if (!skillRepository.existsByIdAndUserId(skillId, userId)) {
            throw new ResourceNotFoundException("Skill not found with id " + skillId + " for user " + userId);
        }
        skillRepository.deleteById(skillId); // Simple delete by ID after check
    }

    // --- Helper Mapping Methods ---
    private SkillDTO convertToDto(Skill skill) {
        SkillDTO dto = new SkillDTO();
        // dto.setId(skill.getId()); // Optional
        dto.setName(skill.getName());
        dto.setCategory(skill.getCategory());
        dto.setIcon(skill.getIcon());
        return dto;
    }

    private void updateEntityFromDto(Skill skill, SkillDTO dto) {
        skill.setName(dto.getName());
        skill.setCategory(dto.getCategory());
        skill.setIcon(dto.getIcon());
        // User is set during creation or already exists
    }
}
