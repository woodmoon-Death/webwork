package com.example.social.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MessageRequest {
  @NotNull(message = "接收方不能为空")
  private Long receiverId;

  @NotBlank(message = "消息内容不能为空")
  @Size(max = 500, message = "消息内容不能超过500个字符")
  private String content;

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
