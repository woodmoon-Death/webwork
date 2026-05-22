package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.dto.CommentRequest;
import com.example.social.security.CurrentUser;
import com.example.social.service.CommentService;
import com.example.social.vo.CommentVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class CommentController {
  private final CommentService commentService;
  private final CurrentUser currentUser;

  public CommentController(CommentService commentService, CurrentUser currentUser) {
    this.commentService = commentService;
    this.currentUser = currentUser;
  }

  @GetMapping("/api/posts/{postId}/comments")
  public ApiResponse<List<CommentVo>> list(@PathVariable Long postId) {
    return ApiResponse.success(commentService.list(postId));
  }

  @PostMapping("/api/posts/{postId}/comments")
  public ApiResponse<CommentVo> create(@PathVariable Long postId, @Valid @RequestBody CommentRequest commentRequest, HttpServletRequest request) {
    return ApiResponse.success(commentService.create(postId, commentRequest, currentUser.required(request)));
  }

  @DeleteMapping("/api/comments/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id, HttpServletRequest request) {
    commentService.delete(id, currentUser.required(request));
    return ApiResponse.success(null);
  }
}
