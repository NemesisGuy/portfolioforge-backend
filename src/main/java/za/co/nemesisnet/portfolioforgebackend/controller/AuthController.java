package za.co.nemesisnet.portfolioforgebackend.controller;


import jakarta.validation.Valid; // For validating request body
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.AuthResponseDto;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.LoginDto;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.RegisterDto;
import za.co.nemesisnet.portfolioforgebackend.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth") // Base path for auth endpoints
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:5173") // Add later for frontend interaction
public class AuthController {

    private final AuthService authService;

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        try {
            String responseMessage = authService.registerUser(registerDto);
            // Return 201 Created for successful registration
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
        } catch (RuntimeException e) {
            // Catch potential exceptions (like username/email exists)
            // Consider a global exception handler (@ControllerAdvice) later for cleaner error handling
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginDto loginDto) {
        // AuthenticationManager handles BadCredentialsException if login fails
        // We might want a @ControllerAdvice to handle that globally later
        AuthResponseDto authResponse = authService.loginUser(loginDto);
        return ResponseEntity.ok(authResponse);
    }
    //get all users
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(authService.getAllUsers());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
