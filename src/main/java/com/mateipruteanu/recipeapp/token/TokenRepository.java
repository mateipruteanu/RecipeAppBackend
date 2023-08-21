package com.mateipruteanu.recipeapp.token;

import com.mateipruteanu.recipeapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.user = ?1 AND t.expired = false AND t.revoked = false")
    List<Token> findAllValidTokensByUser(User user);

    Optional<Token> findByToken(String token);
}
