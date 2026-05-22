package com.example.social.vo;

public class NotificationSummaryVo {
  private int unreadNotifications;
  private int unreadMessages;

  public NotificationSummaryVo() {
  }

  public NotificationSummaryVo(int unreadNotifications, int unreadMessages) {
    this.unreadNotifications = unreadNotifications;
    this.unreadMessages = unreadMessages;
  }

  public int getUnreadNotifications() {
    return unreadNotifications;
  }

  public void setUnreadNotifications(int unreadNotifications) {
    this.unreadNotifications = unreadNotifications;
  }

  public int getUnreadMessages() {
    return unreadMessages;
  }

  public void setUnreadMessages(int unreadMessages) {
    this.unreadMessages = unreadMessages;
  }
}
