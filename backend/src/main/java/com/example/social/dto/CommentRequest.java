package com.example.social.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentRequest {
  @NotBlank(message = "评论不能为空")
  @Size(max = 300, message = "评论不能超过 300 个字符")
  private String content;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
