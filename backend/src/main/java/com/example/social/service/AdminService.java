package com.example.social.service;

import com.example.social.mapper.CommentMapper;
import com.example.social.mapper.LikeMapper;
import com.example.social.mapper.PostMapper;
import com.example.social.mapper.UserMapper;
import com.example.social.vo.AdminStatsVo;
import com.example.social.vo.PostVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
  private final UserMapper userMapper;
  private final PostMapper postMapper;
  private final CommentMapper commentMapper;
  private final LikeMapper likeMapper;

  public AdminService(UserMapper userMapper, PostMapper postMapper, CommentMapper commentMapper, LikeMapper likeMapper) {
    this.userMapper = userMapper;
    this.postMapper = postMapper;
    this.commentMapper = commentMapper;
    this.likeMapper = likeMapper;
  }

  public AdminStatsVo stats() {
    AdminStatsVo stats = new AdminStatsVo();
    stats.setUserCount(userMapper.countUsers());
    stats.setPostCount(postMapper.countPosts());
    stats.setCommentCount(commentMapper.countComments());
    stats.setLikeCount(likeMapper.countLikes());
    stats.setHiddenPostCount(postMapper.countHiddenPosts());
    return stats;
  }

  public List<PostVo> posts(Long viewerId) {
    return postMapper.findFeed(null, "admin", viewerId, true);
  }
}
