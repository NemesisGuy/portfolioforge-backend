package za.co.nemesisnet.portfolioforgebackend.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List; // For validation errors

@Getter
@Setter
@AllArgsConstructor // Constructor with all args
@NoArgsConstructor // Default constructor
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details; // e.g., Request URI
    private List<String> errors; // Specifically for validation errors

    // Constructor without validation errors list
    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}