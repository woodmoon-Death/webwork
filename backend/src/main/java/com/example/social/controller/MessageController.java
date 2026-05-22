package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.dto.MessageRequest;
import com.example.social.entity.User;
import com.example.social.security.CurrentUser;
import com.example.social.service.MessageService;
import com.example.social.vo.DirectMessageVo;
import com.example.social.vo.MessageThreadVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
  private final MessageService messageService;
  private final CurrentUser currentUser;

  public MessageController(MessageService messageService, CurrentUser currentUser) {
    this.messageService = messageService;
    this.currentUser = currentUser;
  }

  @GetMapping("/threads")
  public ApiResponse<List<MessageThreadVo>> threads(HttpServletRequest request) {
    User user = currentUser.required(request);
    return ApiResponse.success(messageService.threads(user));
  }

  @GetMapping("/with/{userId}")
  public ApiResponse<List<DirectMessageVo>> conversation(@PathVariable Long userId, HttpServletRequest request) {
    User user = currentUser.required(request);
    return ApiResponse.success(messageService.conversation(user, userId));
  }

  @PostMapping
  public ApiResponse<DirectMessageVo> send(@Valid @RequestBody MessageRequest messageRequest, HttpServletRequest request) {
    User user = currentUser.required(request);
    return ApiResponse.success(messageService.send(user, messageRequest));
  }

  @DeleteMapping("/threads/{userId}")
  public ApiResponse<Void> hideThread(@PathVariable Long userId, HttpServletRequest request) {
    User user = currentUser.required(request);
    messageService.hideThread(user, userId);
    return ApiResponse.success(null);
  }
}
