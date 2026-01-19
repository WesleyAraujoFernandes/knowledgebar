package com.knowledgebar.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgebar.domain.model.security.RefreshToken;
import com.knowledgebar.domain.model.security.jwt.JwtService;
import com.knowledgebar.domain.repository.RefreshTokenRepository;
import com.knowledgebar.domain.repository.UserRepository;
import com.knowledgebar.dto.request.LoginRequest;
import com.knowledgebar.dto.request.LoginResponse;
import com.knowledgebar.dto.request.RefreshTokenRequest;
import com.knowledgebar.exception.UnauthorizedException;
import com.knowledgebar.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(AuthenticationManager authenticationManager,
            JwtService jwtService, UserRepository userRepository, RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(
                        @Valid @RequestBody LoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.email(),
                                                request.password()));

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow();

                String accessToken = jwtService.generateToken(userDetails);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                String role = userDetails.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(a -> a.getAuthority())
                                .orElse("ROLE_USER");

                return ResponseEntity.ok(
                                new LoginResponse(
                                                accessToken,
                                                refreshToken.getToken(),
                                                user.getUsername(),
                                                role));
        }

        @PostMapping("/refresh")
        public ResponseEntity<LoginResponse> refresh(
                        @RequestBody RefreshTokenRequest request) {

                RefreshToken oldToken = refreshTokenService.validate(request.refreshToken());

                if (oldToken.isRevoked()) {
                        throw new UnauthorizedException("Refresh token reutilizado");
                }

                // Revoga o token antigo
                oldToken.setRevoked(true);
                refreshTokenRepository.save(oldToken);

                // Gera novo refresh token
                RefreshToken newToken = refreshTokenService.createRefreshToken(
                                oldToken.getUser());

                String accessToken = jwtService.generateAccessToken(oldToken.getUser());

                return ResponseEntity.ok(
                                new LoginResponse(
                                                accessToken,
                                                newToken.getToken(),
                                                oldToken.getUser().getUsername(),
                                                oldToken.getUser().getRole().name()));
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout(HttpServletRequest request) {

                String refreshToken = CookieUtils.getRefreshToken(request);

                if (refreshToken != null) {
                        refreshTokenService.revokeAllByRefreshToken(refreshToken);
                }

                ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                                .httpOnly(true)
                                .secure(true)
                                .sameSite("Strict")
                                .path("/auth/refresh")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .build();
        }

}
