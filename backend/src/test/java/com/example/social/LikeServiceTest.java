package com.example.social;

import com.example.social.entity.User;
import com.example.social.mapper.LikeMapper;
import com.example.social.mapper.PostMapper;
import com.example.social.mapper.UserMapper;
import com.example.social.service.LikeService;
import com.example.social.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeServiceTest {
  @Test
  void likingSamePostTwiceKeepsSingleLikeThenUnlikeRemovesIt() {
    FakeLikeMapper mapper = new FakeLikeMapper();
    LikeService service = new LikeService(mapper, nullPostMapper(), nullUserMapper(), nullNotificationService());
    User user = new User();
    user.setId(7L);

    service.like(3L, user);
    service.like(3L, user);

    assertEquals(1, mapper.likes.size());

    service.unlike(3L, user);

    assertEquals(0, mapper.likes.size());
  }

  private PostMapper nullPostMapper() {
    return null;
  }

  private UserMapper nullUserMapper() {
    return null;
  }

  private NotificationService nullNotificationService() {
    return null;
  }

  static class FakeLikeMapper implements LikeMapper {
    Set<String> likes = new HashSet<>();

    @Override
    public int insertIgnore(Long postId, Long userId) {
      likes.add(postId + ":" + userId);
      return 1;
    }

    @Override
    public int delete(Long postId, Long userId) {
      likes.remove(postId + ":" + userId);
      return 1;
    }

    @Override
    public int countByPostId(Long postId) {
      return likes.size();
    }

    @Override
    public int exists(Long postId, Long userId) {
      return likes.contains(postId + ":" + userId) ? 1 : 0;
    }

    @Override
    public int countLikes() {
      return likes.size();
    }
  }
}
