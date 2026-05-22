package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.entity.User;
import com.example.social.security.CurrentUser;
import com.example.social.service.NotificationService;
import com.example.social.vo.NotificationSummaryVo;
import com.example.social.vo.NotificationVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService notificationService;
  private final CurrentUser currentUser;

  public NotificationController(NotificationService notificationService, CurrentUser currentUser) {
    this.notificationService = notificationService;
    this.currentUser = currentUser;
  }

  @GetMapping
  public ApiResponse<List<NotificationVo>> list(HttpServletRequest request) {
    User user = currentUser.required(request);
    return ApiResponse.success(notificationService.list(user));
  }

  @GetMapping("/summary")
  public ApiResponse<NotificationSummaryVo> summary(HttpServletRequest request) {
    User user = currentUser.required(request);
    return ApiResponse.success(notificationService.summary(user));
  }

  @PostMapping("/read-all")
  public ApiResponse<Void> markAllRead(HttpServletRequest request) {
    User user = currentUser.required(request);
    notificationService.markAllRead(user);
    return ApiResponse.success(null);
  }
}
