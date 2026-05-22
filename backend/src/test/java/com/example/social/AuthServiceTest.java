package com.example.social;

import com.example.social.dto.RegisterRequest;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.mapper.UserMapper;
import com.example.social.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
  @Test
  void registerHashesPasswordAndRejectsDuplicateUsername() {
    FakeUserMapper mapper = new FakeUserMapper();
    AuthService service = new AuthService(mapper, new BCryptPasswordEncoder());

    RegisterRequest request = new RegisterRequest();
    request.setUsername("alice");
    request.setNickname("Alice");
    request.setPassword("secret123");
    request.setConfirmPassword("secret123");

    service.register(request);

    assertNotNull(mapper.saved);
    assertEquals("alice", mapper.saved.getUsername());
    assertNotEquals("secret123", mapper.saved.getPasswordHash());
    assertTrue(new BCryptPasswordEncoder().matches("secret123", mapper.saved.getPasswordHash()));
    assertThrows(BusinessException.class, () -> service.register(request));
  }

  static class FakeUserMapper implements UserMapper {
    User saved;

    @Override
    public User findByUsername(String username) {
      return saved != null && saved.getUsername().equals(username) ? saved : null;
    }

    @Override
    public User findById(Long id) {
      return saved != null && saved.getId().equals(id) ? saved : null;
    }

    @Override
    public int insert(User user) {
      user.setId(1L);
      saved = user;
      return 1;
    }

    @Override
    public int updateProfile(Long id, String nickname, String bio, String avatarUrl) {
      return 0;
    }

    @Override
    public int countReceivedLikes(Long id) {
      return 0;
    }

    @Override
    public int countUsers() {
      return saved == null ? 0 : 1;
    }
  }
}
