package com.example.social.service;

import com.example.social.entity.User;
import com.example.social.mapper.LikeMapper;
import com.example.social.mapper.PostMapper;
import com.example.social.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
  private final LikeMapper likeMapper;
  private final PostMapper postMapper;
  private final UserMapper userMapper;
  private final NotificationService notificationService;

  public LikeService(LikeMapper likeMapper, PostMapper postMapper, UserMapper userMapper, NotificationService notificationService) {
    this.likeMapper = likeMapper;
    this.postMapper = postMapper;
    this.userMapper = userMapper;
    this.notificationService = notificationService;
  }

  public int like(Long postId, User user) {
    int inserted = likeMapper.insertIgnore(postId, user.getId());
    if (inserted > 0) {
      com.example.social.entity.Post post = postMapper.findEntityById(postId);
      if (post != null) {
        notificationService.notifyLike(userMapper.findById(post.getUserId()), user, postId, post.getTitle());
      }
    }
    return likeMapper.countByPostId(postId);
  }

  public int unlike(Long postId, User user) {
    likeMapper.delete(postId, user.getId());
    return likeMapper.countByPostId(postId);
  }
}
