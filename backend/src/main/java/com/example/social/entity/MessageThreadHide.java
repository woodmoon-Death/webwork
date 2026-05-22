package com.example.social.entity;

public class MessageThreadHide {
  private Long id;
  private Long userId;
  private Long partnerId;
  private Long hiddenAfterMessageId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(Long partnerId) {
    this.partnerId = partnerId;
  }

  public Long getHiddenAfterMessageId() {
    return hiddenAfterMessageId;
  }

  public void setHiddenAfterMessageId(Long hiddenAfterMessageId) {
    this.hiddenAfterMessageId = hiddenAfterMessageId;
  }
}
