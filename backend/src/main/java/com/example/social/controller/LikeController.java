package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.security.CurrentUser;
import com.example.social.service.LikeService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/like")
public class LikeController {
  private final LikeService likeService;
  private final CurrentUser currentUser;

  public LikeController(LikeService likeService, CurrentUser currentUser) {
    this.likeService = likeService;
    this.currentUser = currentUser;
  }

  @PostMapping
  public ApiResponse<Map<String, Integer>> like(@PathVariable Long postId, HttpServletRequest request) {
    Map<String, Integer> data = new HashMap<>();
    data.put("likeCount", likeService.like(postId, currentUser.required(request)));
    return ApiResponse.success(data);
  }

  @DeleteMapping
  public ApiResponse<Map<String, Integer>> unlike(@PathVariable Long postId, HttpServletRequest request) {
    Map<String, Integer> data = new HashMap<>();
    data.put("likeCount", likeService.unlike(postId, currentUser.required(request)));
    return ApiResponse.success(data);
  }
}
