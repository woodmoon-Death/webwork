package com.example.social;

import com.example.social.entity.Comment;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.security.Role;
import com.example.social.service.PermissionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PermissionServiceTest {
  private final PermissionService service = new PermissionService();

  @Test
  void postOwnerAndAdminCanDeletePostButOtherUserCannot() {
    Post post = new Post();
    post.setUserId(10L);

    assertDoesNotThrow(() -> service.requirePostOwnerOrAdmin(user(10L, Role.USER), post));
    assertDoesNotThrow(() -> service.requirePostOwnerOrAdmin(user(99L, Role.ADMIN), post));
    assertThrows(BusinessException.class, () -> service.requirePostOwnerOrAdmin(user(99L, Role.USER), post));
  }

  @Test
  void commentOwnerAndAdminCanDeleteCommentButOtherUserCannot() {
    Comment comment = new Comment();
    comment.setUserId(10L);

    assertDoesNotThrow(() -> service.requireCommentOwnerOrAdmin(user(10L, Role.USER), comment));
    assertDoesNotThrow(() -> service.requireCommentOwnerOrAdmin(user(99L, Role.ADMIN), comment));
    assertThrows(BusinessException.class, () -> service.requireCommentOwnerOrAdmin(user(99L, Role.USER), comment));
  }

  private User user(Long id, Role role) {
    User user = new User();
    user.setId(id);
    user.setRole(role.name());
    return user;
  }
}
