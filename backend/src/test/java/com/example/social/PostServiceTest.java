package com.example.social;

import com.example.social.exception.BusinessException;
import com.example.social.service.PostService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostServiceTest {
  private final PostService service = new PostService(null, null, null);

  @Test
  void normalizeTagsTrimsDeduplicatesAndJoinsAllowedTags() {
    String result = service.normalizeTags(Arrays.asList(" 学习记录 ", "图片分享", "学习记录", ""));

    assertEquals("学习记录,图片分享", result);
  }

  @Test
  void normalizeTagsRejectsTooManyOrUnknownTags() {
    assertThrows(BusinessException.class, () -> service.normalizeTags(Arrays.asList("校园日常", "学习记录", "图片分享", "生活灵感")));
    assertThrows(BusinessException.class, () -> service.normalizeTags(Arrays.asList("陌生标签")));
  }
}
