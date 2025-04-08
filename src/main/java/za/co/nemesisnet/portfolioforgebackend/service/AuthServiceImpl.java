package za.co.nemesisnet.portfolioforgebackend.service;

import za.co.nemesisnet.portfolioforgebackend.domain.User;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.AuthResponseDto;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.LoginDto;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.RegisterDto;
import za.co.nemesisnet.portfolioforgebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.nemesisnet.portfolioforgebackend.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Inject AuthenticationManager
    private final JwtTokenProvider jwtTokenProvider; // <<< Inject JwtTokenProvider

    @Override
    @Transactional // Ensure operation is atomic
    public String registerUser(RegisterDto registerDto) {
        // ... registration logic remains the same ...
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "User registered successfully!";
    }


    @Override
    public AuthResponseDto loginUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // --- Generate JWT Token ---
        String token = jwtTokenProvider.generateToken(authentication); // <<< Generate token

        // --- Return token in response ---
        return new AuthResponseDto(token); // <<< Return DTO with token
    }
    public int getAllUsers() {
        return userRepository.findAll().size();
    }
}
