package za.co.nemesisnet.portfolioforgebackend.domain.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReadStatusRequestDTO {

    // Use Boolean wrapper to handle potential null in request if needed,
    // but @NotNull ensures it must be provided.
    @NotNull(message = "Read status (isRead) must be provided (true or false).")
    private Boolean isRead;
}