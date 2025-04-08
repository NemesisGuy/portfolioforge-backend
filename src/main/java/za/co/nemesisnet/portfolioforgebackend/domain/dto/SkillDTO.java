package za.co.nemesisnet.portfolioforgebackend.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillDTO {

    // private Long id; // Optional: include in response if needed by frontend

    @NotBlank(message = "Skill name cannot be blank")
    @Size(max = 100, message = "Skill name must be less than 100 characters")
    private String name;

    @Size(max = 100, message = "Category must be less than 100 characters")
    private String category;

    @Size(max = 100, message = "Icon reference must be less than 100 characters")
    private String icon;
}