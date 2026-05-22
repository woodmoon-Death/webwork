package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.dto.LoginRequest;
import com.example.social.dto.RegisterRequest;
import com.example.social.entity.User;
import com.example.social.security.CurrentUser;
import com.example.social.service.AuthService;
import com.example.social.vo.AuthVo;
import com.example.social.vo.UserVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final CurrentUser currentUser;

  public AuthController(AuthService authService, CurrentUser currentUser) {
    this.authService = authService;
    this.currentUser = currentUser;
  }

  @PostMapping("/register")
  public ApiResponse<UserVo> register(@Valid @RequestBody RegisterRequest request) {
    return ApiResponse.success(authService.register(request));
  }

  @PostMapping("/login")
  public ApiResponse<AuthVo> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.success(authService.login(request));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout() {
    return ApiResponse.success(null);
  }

  @GetMapping("/me")
  public ApiResponse<UserVo> me(HttpServletRequest request) {
    User user = currentUser.required(request);
    return ApiResponse.success(UserVo.from(user));
  }
}
