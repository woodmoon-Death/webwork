package com.example.social.vo;

public class AdminStatsVo {
  private int userCount;
  private int postCount;
  private int commentCount;
  private int likeCount;
  private int hiddenPostCount;

  public int getUserCount() {
    return userCount;
  }

  public void setUserCount(int userCount) {
    this.userCount = userCount;
  }

  public int getPostCount() {
    return postCount;
  }

  public void setPostCount(int postCount) {
    this.postCount = postCount;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public int getHiddenPostCount() {
    return hiddenPostCount;
  }

  public void setHiddenPostCount(int hiddenPostCount) {
    this.hiddenPostCount = hiddenPostCount;
  }
}
