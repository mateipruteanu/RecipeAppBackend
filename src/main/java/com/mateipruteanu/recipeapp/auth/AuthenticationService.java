package com.mateipruteanu.recipeapp.auth;

import com.mateipruteanu.recipeapp.config.JwtService;
import com.mateipruteanu.recipeapp.models.Role;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegistrationRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        //   the user is authenticated
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .build();
    }
}
