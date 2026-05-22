package com.example.social;

import com.example.social.entity.Notification;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.mapper.DirectMessageMapper;
import com.example.social.mapper.LikeMapper;
import com.example.social.mapper.NotificationMapper;
import com.example.social.mapper.PostMapper;
import com.example.social.mapper.UserMapper;
import com.example.social.service.LikeService;
import com.example.social.service.NotificationService;
import com.example.social.vo.DirectMessageVo;
import com.example.social.vo.MessageThreadVo;
import com.example.social.vo.NotificationVo;
import com.example.social.vo.PostVo;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeServiceTest {
  @Test
  void likingSamePostTwiceKeepsSingleLikeThenUnlikeRemovesIt() {
    FakeLikeMapper mapper = new FakeLikeMapper();
    FakeNotificationMapper notificationMapper = new FakeNotificationMapper();
    LikeService service = new LikeService(mapper, fakePostMapper(), fakeUserMapper(), new NotificationService(notificationMapper, fakeDirectMessageMapper()));
    User user = new User();
    user.setId(7L);
    user.setNickname("liker");

    service.like(3L, user);
    service.like(3L, user);

    assertEquals(1, mapper.likes.size());
    assertEquals(1, notificationMapper.count);

    service.unlike(3L, user);

    assertEquals(0, mapper.likes.size());
  }

  private PostMapper fakePostMapper() {
    return new PostMapper() {
      @Override
      public int insert(Post post) {
        return 0;
      }

      @Override
      public int update(Post post) {
        return 0;
      }

      @Override
      public int deleteById(Long id) {
        return 0;
      }

      @Override
      public Post findEntityById(Long id) {
        Post post = new Post();
        post.setId(id);
        post.setUserId(9L);
        post.setTitle("测试分享");
        return post;
      }

      @Override
      public PostVo findVoById(Long id, Long viewerId, boolean includeHidden) {
        return null;
      }

      @Override
      public List<PostVo> findFeed(String keyword, String filter, Long viewerId, boolean includeHidden) {
        return Collections.emptyList();
      }

      @Override
      public List<PostVo> findByUserId(Long userId, Long viewerId) {
        return Collections.emptyList();
      }

      @Override
      public int updateVisibility(Long id, String visibility) {
        return 0;
      }

      @Override
      public int countPosts() {
        return 0;
      }

      @Override
      public int countHiddenPosts() {
        return 0;
      }
    };
  }

  private UserMapper fakeUserMapper() {
    return new UserMapper() {
      @Override
      public User findByUsername(String username) {
        return null;
      }

      @Override
      public User findById(Long id) {
        User user = new User();
        user.setId(id);
        user.setNickname("author");
        return user;
      }

      @Override
      public int insert(User user) {
        return 0;
      }

      @Override
      public int updateProfile(Long id, String nickname, String bio, String avatarUrl) {
        return 0;
      }

      @Override
      public int countReceivedLikes(Long id) {
        return 0;
      }

      @Override
      public int countUsers() {
        return 0;
      }
    };
  }

  private DirectMessageMapper fakeDirectMessageMapper() {
    return new DirectMessageMapper() {
      @Override
      public int insert(com.example.social.entity.DirectMessage message) {
        return 0;
      }

      @Override
      public List<DirectMessageVo> findConversation(Long userId, Long otherUserId) {
        return Collections.emptyList();
      }

      @Override
      public List<MessageThreadVo> findThreads(Long userId) {
        return Collections.emptyList();
      }

      @Override
      public Long findLatestMessageId(Long userId, Long otherUserId) {
        return null;
      }

      @Override
      public int markConversationRead(Long userId, Long otherUserId) {
        return 0;
      }

      @Override
      public int countUnreadByReceiver(Long userId) {
        return 0;
      }
    };
  }

  static class FakeLikeMapper implements LikeMapper {
    Set<String> likes = new HashSet<>();

    @Override
    public int insertIgnore(Long postId, Long userId) {
      return likes.add(postId + ":" + userId) ? 1 : 0;
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

  static class FakeNotificationMapper implements NotificationMapper {
    int count;

    @Override
    public int insert(Notification notification) {
      count += 1;
      return 1;
    }

    @Override
    public List<NotificationVo> findByUserId(Long userId) {
      return Collections.emptyList();
    }

    @Override
    public int countUnreadByUserId(Long userId) {
      return 0;
    }

    @Override
    public int markAllRead(Long userId) {
      return 0;
    }

    @Override
    public int markMessageNotificationsRead(Long userId, Long actorId) {
      return 0;
    }
  }
}
