package za.co.nemesisnet.portfolioforgebackend.service;


import za.co.nemesisnet.portfolioforgebackend.domain.dto.AuthResponseDto;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.LoginDto;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.RegisterDto;

public interface AuthService {

    /**
     * Registers a new user.
     * @param registerDto DTO containing registration details.
     * @return A message indicating successful registration.
     * @throws RuntimeException if username or email already exists. // Consider custom exceptions later
     */
    String registerUser(RegisterDto registerDto);

    /**
     * Authenticates a user and potentially returns authentication details (like a token).
     * @param loginDto DTO containing login credentials.
     * @return AuthResponseDto containing authentication results (e.g., token).
     */
    AuthResponseDto loginUser(LoginDto loginDto);
    int getAllUsers();
}
