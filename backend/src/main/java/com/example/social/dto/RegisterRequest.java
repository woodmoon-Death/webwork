package com.example.social.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RegisterRequest {
  @NotBlank(message = "用户名不能为空")
  @Size(min = 3, max = 30, message = "用户名长度应为 3-30 个字符")
  private String username;

  @NotBlank(message = "昵称不能为空")
  @Size(max = 30, message = "昵称不能超过 30 个字符")
  private String nickname;

  @NotBlank(message = "密码不能为空")
  @Size(min = 6, max = 60, message = "密码长度应为 6-60 个字符")
  private String password;

  @NotBlank(message = "确认密码不能为空")
  private String confirmPassword;

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}
