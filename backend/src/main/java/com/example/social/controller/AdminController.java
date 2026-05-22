package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.entity.User;
import com.example.social.security.CurrentUser;
import com.example.social.service.AdminService;
import com.example.social.vo.AdminStatsVo;
import com.example.social.vo.PostVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final AdminService adminService;
  private final CurrentUser currentUser;

  public AdminController(AdminService adminService, CurrentUser currentUser) {
    this.adminService = adminService;
    this.currentUser = currentUser;
  }

  @GetMapping("/stats")
  public ApiResponse<AdminStatsVo> stats(HttpServletRequest request) {
    currentUser.admin(request);
    return ApiResponse.success(adminService.stats());
  }

  @GetMapping("/posts")
  public ApiResponse<List<PostVo>> posts(HttpServletRequest request) {
    User admin = currentUser.admin(request);
    return ApiResponse.success(adminService.posts(admin.getId()));
  }
}
