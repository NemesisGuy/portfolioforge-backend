package za.co.nemesisnet.portfolioforgebackend.domain.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "Username or email cannot be blank")
    private String usernameOrEmail; // Allow login with either

    @NotBlank(message = "Password cannot be blank")
    private String password;
}