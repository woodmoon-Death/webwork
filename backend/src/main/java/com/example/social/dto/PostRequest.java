package com.example.social.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class PostRequest {
  @NotBlank(message = "标题不能为空")
  @Size(min = 2, max = 80, message = "标题长度应为 2-80 个字符")
  private String title;

  @NotBlank(message = "正文不能为空")
  @Size(max = 1000, message = "正文不能超过 1000 个字符")
  private String content;

  private String imageUrl;

  private List<String> tags;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
}
