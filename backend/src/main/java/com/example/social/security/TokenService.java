package com.example.social.security;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class TokenService {
  public String createToken(Long userId) {
    String payload = userId + ":" + UUID.randomUUID();
    return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
  }

  public Long parseUserId(String token) {
    if (token == null || token.trim().isEmpty()) {
      return null;
    }
    try {
      String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
      return Long.parseLong(decoded.split(":")[0]);
    } catch (RuntimeException exception) {
      return null;
    }
  }
}
