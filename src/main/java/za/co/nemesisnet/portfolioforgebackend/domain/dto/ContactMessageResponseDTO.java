package za.co.nemesisnet.portfolioforgebackend.domain.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContactMessageResponseDTO {
    private Long id; // Include ID for potential operations like marking as read
    private String senderName;
    private String senderEmail;
    private String subject;
    private String message;
    private LocalDateTime submissionDate;
    private boolean isRead;
    // We don't include the recipient User object
}
