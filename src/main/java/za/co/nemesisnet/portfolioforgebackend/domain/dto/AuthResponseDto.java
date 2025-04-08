package za.co.nemesisnet.portfolioforgebackend.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    // Remove the simple message field or keep it if desired
    // private String message;
    private String accessToken;
    private String tokenType = "Bearer"; // Standard prefix for Bearer tokens

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
