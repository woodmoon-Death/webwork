package com.example.social.service;

import com.example.social.dto.LoginRequest;
import com.example.social.dto.RegisterRequest;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.mapper.UserMapper;
import com.example.social.security.Role;
import com.example.social.security.TokenService;
import com.example.social.vo.AuthVo;
import com.example.social.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
    this(userMapper, passwordEncoder, new TokenService());
  }

  @Autowired
  public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, TokenService tokenService) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
  }

  public UserVo register(RegisterRequest request) {
    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new BusinessException("两次输入的密码不一致");
    }
    if (userMapper.findByUsername(request.getUsername()) != null) {
      throw new BusinessException("用户名已存在");
    }
    User user = new User();
    user.setUsername(request.getUsername().trim());
    user.setNickname(request.getNickname().trim());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER.name());
    user.setStatus("ACTIVE");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    userMapper.insert(user);
    return UserVo.from(user);
  }

  public AuthVo login(LoginRequest request) {
    User user = userMapper.findByUsername(request.getUsername());
    if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new BusinessException(401, "用户名或密码错误");
    }
    if (!"ACTIVE".equals(user.getStatus())) {
      throw new BusinessException(403, "账号不可用");
    }
    return new AuthVo(tokenService.createToken(user.getId()), UserVo.from(user));
  }
}
