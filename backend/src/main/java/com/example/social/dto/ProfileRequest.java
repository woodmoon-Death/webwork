package com.example.social.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProfileRequest {
  @NotBlank(message = "昵称不能为空")
  @Size(max = 30, message = "昵称不能超过 30 个字符")
  private String nickname;

  @Size(max = 255, message = "简介不能超过 255 个字符")
  private String bio;

  private String avatarUrl;

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}
