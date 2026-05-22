package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.dto.ProfileRequest;
import com.example.social.entity.User;
import com.example.social.security.CurrentUser;
import com.example.social.service.UserService;
import com.example.social.vo.PostVo;
import com.example.social.vo.UserVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  private final CurrentUser currentUser;

  public UserController(UserService userService, CurrentUser currentUser) {
    this.userService = userService;
    this.currentUser = currentUser;
  }

  @GetMapping("/{id}")
  public ApiResponse<UserVo> detail(@PathVariable Long id) {
    return ApiResponse.success(userService.findById(id));
  }

  @PutMapping("/me")
  public ApiResponse<UserVo> updateMe(@Valid @RequestBody ProfileRequest profileRequest, HttpServletRequest request) {
    return ApiResponse.success(userService.updateMe(currentUser.required(request), profileRequest));
  }

  @GetMapping("/{id}/posts")
  public ApiResponse<List<PostVo>> posts(@PathVariable Long id, HttpServletRequest request) {
    User viewer = currentUser.optional(request);
    return ApiResponse.success(userService.posts(id, viewer));
  }
}
