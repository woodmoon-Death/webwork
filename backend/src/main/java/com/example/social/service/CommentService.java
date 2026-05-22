package com.example.social.service;

import com.example.social.dto.CommentRequest;
import com.example.social.entity.Comment;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.mapper.CommentMapper;
import com.example.social.mapper.PostMapper;
import com.example.social.mapper.UserMapper;
import com.example.social.vo.CommentVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
  private final CommentMapper commentMapper;
  private final PostMapper postMapper;
  private final UserMapper userMapper;
  private final PermissionService permissionService;
  private final NotificationService notificationService;

  public CommentService(CommentMapper commentMapper, PostMapper postMapper, UserMapper userMapper,
                        PermissionService permissionService, NotificationService notificationService) {
    this.commentMapper = commentMapper;
    this.postMapper = postMapper;
    this.userMapper = userMapper;
    this.permissionService = permissionService;
    this.notificationService = notificationService;
  }

  public List<CommentVo> list(Long postId) {
    return commentMapper.findByPostId(postId);
  }

  public CommentVo create(Long postId, CommentRequest request, User user) {
    Post post = postMapper.findEntityById(postId);
    Comment comment = new Comment();
    comment.setPostId(postId);
    comment.setUserId(user.getId());
    comment.setContent(request.getContent().trim());
    comment.setCreatedAt(LocalDateTime.now());
    comment.setUpdatedAt(LocalDateTime.now());
    commentMapper.insert(comment);
    if (post != null) {
      notificationService.notifyComment(userMapper.findById(post.getUserId()), user, postId, comment.getId(), post.getTitle());
    }
    List<CommentVo> comments = list(postId);
    return comments.stream().filter(item -> item.getId().equals(comment.getId())).findFirst().orElse(null);
  }

  public void delete(Long id, User user) {
    Comment comment = commentMapper.findEntityById(id);
    permissionService.requireCommentOwnerOrAdmin(user, comment);
    commentMapper.deleteById(id);
  }
}
