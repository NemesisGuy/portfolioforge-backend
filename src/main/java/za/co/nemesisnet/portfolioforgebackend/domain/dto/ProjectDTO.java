package za.co.nemesisnet.portfolioforgebackend.domain.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull; // For non-string fields like displayOrder
import lombok.Data;

@Data
public class ProjectDTO {

    // We don't expose ID in request/response generally,
    // but might include it in responses sometimes. Add if needed:
    // private Long id;

    @NotBlank(message = "Project title cannot be blank")
    @Size(max = 100, message = "Project title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Project description cannot be blank")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters") // Example max length
    private String description;

    @Size(max = 255, message = "Technologies list cannot exceed 255 characters")
    private String technologies; // Keep as string for now

    @Size(max = 512, message = "Image URL cannot exceed 512 characters")
    // Add @URL validation if desired: @org.hibernate.validator.constraints.URL
    private String imageUrl;

    @Size(max = 512, message = "Live demo URL cannot exceed 512 characters")
    // @org.hibernate.validator.constraints.URL
    private String liveUrl;

    @Size(max = 512, message = "Repository URL cannot exceed 512 characters")
    // @org.hibernate.validator.constraints.URL
    private String repoUrl;

    @NotNull(message = "Display order cannot be null") // Use NotNull for primitives/wrappers
    private Integer displayOrder = 0; // Use Integer to allow null check, provide default

    // Timestamps (createdAt, updatedAt) are usually handled by server,
    // maybe include read-only updatedAt in response DTO if needed.
}
