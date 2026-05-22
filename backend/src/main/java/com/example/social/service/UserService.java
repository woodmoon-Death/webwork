package com.example.social.service;

import com.example.social.dto.ProfileRequest;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.mapper.PostMapper;
import com.example.social.mapper.UserMapper;
import com.example.social.vo.PostVo;
import com.example.social.vo.UserVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
  private final UserMapper userMapper;
  private final PostMapper postMapper;

  public UserService(UserMapper userMapper, PostMapper postMapper) {
    this.userMapper = userMapper;
    this.postMapper = postMapper;
  }

  public UserVo findById(Long id) {
    User user = userMapper.findById(id);
    if (user == null) {
      throw new BusinessException(404, "用户不存在");
    }
    UserVo vo = UserVo.from(user);
    vo.setReceivedLikeCount(userMapper.countReceivedLikes(id));
    return vo;
  }

  public UserVo updateMe(User user, ProfileRequest request) {
    userMapper.updateProfile(user.getId(), request.getNickname(), request.getBio(), request.getAvatarUrl());
    return findById(user.getId());
  }

  public List<PostVo> posts(Long userId, User viewer) {
    return postMapper.findByUserId(userId, viewer == null ? null : viewer.getId());
  }
}
