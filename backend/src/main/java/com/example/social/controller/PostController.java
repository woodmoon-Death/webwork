package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.dto.PostRequest;
import com.example.social.entity.User;
import com.example.social.security.CurrentUser;
import com.example.social.service.PostService;
import com.example.social.vo.PostVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
  private final PostService postService;
  private final CurrentUser currentUser;

  public PostController(PostService postService, CurrentUser currentUser) {
    this.postService = postService;
    this.currentUser = currentUser;
  }

  @GetMapping
  public ApiResponse<List<PostVo>> feed(@RequestParam(required = false) String keyword, @RequestParam(required = false) String filter, HttpServletRequest request) {
    return ApiResponse.success(postService.feed(keyword, filter, currentUser.optional(request)));
  }

  @PostMapping
  public ApiResponse<PostVo> create(@Valid @RequestBody PostRequest postRequest, HttpServletRequest request) {
    return ApiResponse.success(postService.create(postRequest, currentUser.required(request), request));
  }

  @GetMapping("/{id}")
  public ApiResponse<PostVo> detail(@PathVariable Long id, HttpServletRequest request) {
    return ApiResponse.success(postService.detail(id, currentUser.optional(request)));
  }

  @PutMapping("/{id}")
  public ApiResponse<PostVo> update(@PathVariable Long id, @Valid @RequestBody PostRequest postRequest, HttpServletRequest request) {
    return ApiResponse.success(postService.update(id, postRequest, currentUser.required(request)));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id, HttpServletRequest request) {
    postService.delete(id, currentUser.required(request));
    return ApiResponse.success(null);
  }

  @PatchMapping("/{id}/visibility")
  public ApiResponse<PostVo> visibility(@PathVariable Long id, @RequestBody Map<String, String> payload, HttpServletRequest request) {
    return ApiResponse.success(postService.visibility(id, payload.get("visibility"), currentUser.admin(request)));
  }
}
