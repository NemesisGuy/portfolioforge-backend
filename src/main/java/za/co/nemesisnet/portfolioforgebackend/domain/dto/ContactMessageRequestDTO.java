package za.co.nemesisnet.portfolioforgebackend.domain.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactMessageRequestDTO {

    @NotBlank(message = "Your name cannot be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String senderName;

    @NotBlank(message = "Your email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String senderEmail;

    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    private String message;
}
