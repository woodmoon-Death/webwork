package com.example.social.service;

import com.example.social.entity.Comment;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.security.Role;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
  public boolean isAdmin(User user) {
    return user != null && Role.ADMIN.name().equals(user.getRole());
  }

  public void requirePostOwnerOrAdmin(User user, Post post) {
    if (post == null) {
      throw new BusinessException(404, "分享不存在");
    }
    if (user == null) {
      throw new BusinessException(401, "请先登录");
    }
    if (!isAdmin(user) && !user.getId().equals(post.getUserId())) {
      throw new BusinessException(403, "无权操作该分享");
    }
  }

  public void requireCommentOwnerOrAdmin(User user, Comment comment) {
    if (comment == null) {
      throw new BusinessException(404, "评论不存在");
    }
    if (user == null) {
      throw new BusinessException(401, "请先登录");
    }
    if (!isAdmin(user) && !user.getId().equals(comment.getUserId())) {
      throw new BusinessException(403, "无权操作该评论");
    }
  }
}
