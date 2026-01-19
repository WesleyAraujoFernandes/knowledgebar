package com.knowledgebar.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.knowledgebar.domain.model.security.RefreshToken;
import com.knowledgebar.domain.model.user.User;
import com.knowledgebar.domain.repository.RefreshTokenRepository;
import com.knowledgebar.exception.UnauthorizedException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RefreshTokenService {

    @Value("${security.jwt.refresh-expiration}")
    private Duration refreshTokenDuration;

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public RefreshToken create(User user, String token) {
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(token);
        rt.setExpiryDate(Instant.now().plus(refreshTokenDuration));
        return repository.save(rt);
    }

    public RefreshToken createRefreshToken(User user) {

        // opcional: invalidar tokens antigos
        repository.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(Instant.now().plus(10, ChronoUnit.SECONDS));

        return repository.save(token);
    }

    public RefreshToken validate(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        if (rt.isRevoked() || rt.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expirado ou revogado");
        }

        return rt;
    }

    @Transactional
    public void revokeAllByRefreshToken(String refreshToken) {
        RefreshToken token = repository
                .findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        repository.deleteAllByUserEmail(token.getUser().getUsername());
    }

    public void deleteByUsername(String username) {
        repository.deleteByUserEmail(username);
    }

    public void revoke(String token) {
        repository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }

    public void revokeAll(User user) {
        repository.deleteByUser(user);
    }

    public void deleteByUser(User user) {
        repository.deleteByUser(user);
    }
}
