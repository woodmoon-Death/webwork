package com.example.social.vo;

import com.example.social.entity.User;

public class UserVo {
  private Long id;
  private String username;
  private String nickname;
  private String avatarUrl;
  private String bio;
  private String role;
  private int receivedLikeCount;

  public static UserVo from(User user) {
    if (user == null) {
      return null;
    }
    UserVo vo = new UserVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setNickname(user.getNickname());
    vo.setAvatarUrl(user.getAvatarUrl());
    vo.setBio(user.getBio());
    vo.setRole(user.getRole());
    return vo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public int getReceivedLikeCount() {
    return receivedLikeCount;
  }

  public void setReceivedLikeCount(int receivedLikeCount) {
    this.receivedLikeCount = receivedLikeCount;
  }
}
