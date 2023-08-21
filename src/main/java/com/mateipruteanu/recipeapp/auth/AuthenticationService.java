package com.mateipruteanu.recipeapp.auth;

import com.mateipruteanu.recipeapp.config.JwtService;
import com.mateipruteanu.recipeapp.models.Role;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import com.mateipruteanu.recipeapp.token.Token;
import com.mateipruteanu.recipeapp.token.TokenRepository;
import com.mateipruteanu.recipeapp.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
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
        User savedUser = userRepository.save(user); // persisting the user to the DB
        String jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
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
        String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token); // persisting the token to the DB
    }

    public void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user);

        if(validUserTokens.isEmpty())
            return;
        else
            System.out.println("AuthenticationService: Revoking all user tokens, they are " + validUserTokens.size() + " in number.");

        validUserTokens.forEach(token -> {
                    token.setExpired(true);
                    token.setRevoked(true);
                });
        tokenRepository.saveAll(validUserTokens);
    }
}
