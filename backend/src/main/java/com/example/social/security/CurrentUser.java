package com.example.social.security;

import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.mapper.UserMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CurrentUser {
  private final UserMapper userMapper;
  private final TokenService tokenService;

  public CurrentUser(UserMapper userMapper, TokenService tokenService) {
    this.userMapper = userMapper;
    this.tokenService = tokenService;
  }

  public User optional(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      return null;
    }
    Long userId = tokenService.parseUserId(header.substring(7));
    return userId == null ? null : userMapper.findById(userId);
  }

  public User required(HttpServletRequest request) {
    User user = optional(request);
    if (user == null) {
      throw new BusinessException(401, "请先登录");
    }
    return user;
  }

  public User admin(HttpServletRequest request) {
    User user = required(request);
    if (!Role.ADMIN.name().equals(user.getRole())) {
      throw new BusinessException(403, "需要管理员权限");
    }
    return user;
  }
}
