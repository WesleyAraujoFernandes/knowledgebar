package com.knowledgebar.dto.request;

public record LoginResponse(String accessToken, String refreshToken, String email, String role) {
}
