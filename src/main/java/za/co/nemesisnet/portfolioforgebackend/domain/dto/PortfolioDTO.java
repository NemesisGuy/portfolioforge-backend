package za.co.nemesisnet.portfolioforgebackend.domain.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PortfolioDTO {

    // We don't expose the internal Portfolio ID or the User object usually

    @Size(max = 5000, message = "About Me text cannot exceed 5000 characters") // Example validation
    private String aboutMeText;

    @Size(max = 512, message = "Resume URL cannot exceed 512 characters")
    private String resumeUrl; // Consider URL validation pattern if needed

    @Size(max = 255, message = "LinkedIn URL cannot exceed 255 characters")
    private String linkedInUrl; // Consider URL validation pattern if needed

    @Size(max = 255, message = "GitHub URL cannot exceed 255 characters")
    private String githubUrl; // Consider URL validation pattern if needed

    @Email(message = "Please provide a valid contact email address")
    @Size(max = 255, message = "Contact Email cannot exceed 255 characters")
    private String contactEmail;

    @Size(min=3, max = 50, message = "Public Slug must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Public Slug can only contain lowercase letters, numbers, and single hyphens (not at start/end)") // Example slug validation
    private String publicSlug; // Allow user to set/update their slug

    private LocalDateTime lastUpdatedAt; // Read-only field from server

}