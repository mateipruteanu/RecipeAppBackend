package com.mateipruteanu.recipeapp.config;

import com.mateipruteanu.recipeapp.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // passing the request and response to the next filter
            return;
        }
        jwt = authorizationHeader.substring(7);


        username = jwtService.extractUsername(jwt);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // getting user details from database
            System.out.println("JwtAuthenticationFilter: UserDetails = " + userDetails.getAuthorities());
            boolean isTokenValidInDatabase = tokenRepository.findByToken(jwt)
                    .map(token -> !token.isExpired() && !token.isRevoked()).orElse(false);
            System.out.println("findbytoken" +tokenRepository.findByToken(jwt));
            System.out.println("isExpired" +tokenRepository.findByToken(jwt).map(token -> !token.isExpired()));
            System.out.println("isRevoked" +tokenRepository.findByToken(jwt).map(token -> !token.isRevoked()));

            if(jwtService.isTokenValid(jwt, userDetails) && isTokenValidInDatabase) {
                System.out.println("JwtAuthenticationFilter: Token is valid");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                ); // creating an authentication token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // setting authentication token in security context
                System.out.println("JwtAuthenticationFilter: Authentication token set in security context");
                System.out.println("JwtAuthenticationFilter: Success JWT:" + jwt+":");
            }
            else {
                System.out.println("JwtAuthenticationFilter: Token is invalid");
                System.out.println("JwtAuthenticationFilter: isTokenValidInDB: " + isTokenValidInDatabase);
                System.out.println("JwtAuthenticationFilter: isTokenValid: " + jwtService.isTokenValid(jwt, userDetails));
                System.out.println("JwtAuthenticationFilter: JWT:"+ jwt+":");
            }
        }
        filterChain.doFilter(request, response); // passing the request and response to the next filter
    }
}
