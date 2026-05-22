package com.example.social.vo;

import java.time.LocalDateTime;

public class MessageThreadVo {
  private Long userId;
  private String nickname;
  private String avatarUrl;
  private String lastMessage;
  private LocalDateTime lastMessageAt;
  private Long lastMessageId;
  private int unreadCount;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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

  public String getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  public LocalDateTime getLastMessageAt() {
    return lastMessageAt;
  }

  public void setLastMessageAt(LocalDateTime lastMessageAt) {
    this.lastMessageAt = lastMessageAt;
  }

  public Long getLastMessageId() {
    return lastMessageId;
  }

  public void setLastMessageId(Long lastMessageId) {
    this.lastMessageId = lastMessageId;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }
}
