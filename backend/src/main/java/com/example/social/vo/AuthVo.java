package com.example.social.vo;

public class AuthVo {
  private String token;
  private UserVo user;

  public AuthVo(String token, UserVo user) {
    this.token = token;
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserVo getUser() {
    return user;
  }

  public void setUser(UserVo user) {
    this.user = user;
  }
}
