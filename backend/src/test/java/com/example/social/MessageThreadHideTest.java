package com.example.social;

import com.example.social.entity.DirectMessage;
import com.example.social.entity.MessageThreadHide;
import com.example.social.entity.User;
import com.example.social.mapper.DirectMessageMapper;
import com.example.social.mapper.MessageThreadHideMapper;
import com.example.social.mapper.UserMapper;
import com.example.social.service.MessageService;
import com.example.social.service.NotificationService;
import com.example.social.vo.DirectMessageVo;
import com.example.social.vo.MessageThreadVo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageThreadHideTest {
  @Test
  void hideThreadFiltersTheCurrentUserOnly() {
    FakeDirectMessageMapper directMessageMapper = new FakeDirectMessageMapper();
    FakeMessageThreadHideMapper hideMapper = new FakeMessageThreadHideMapper();
    MessageService service = new MessageService(directMessageMapper, hideMapper, new FakeUserMapper(), new FakeNotificationService());
    User user = user(2L, "GGB");

    List<MessageThreadVo> before = service.threads(user);
    assertEquals(2, before.size());

    service.hideThread(user, 1L);

    List<MessageThreadVo> after = service.threads(user);
    assertEquals(2, after.size());
    assertEquals(1L, after.get(0).getUserId());
    assertEquals(3L, after.get(1).getUserId());
    assertTrue(hideMapper.saved);
  }

  private User user(Long id, String nickname) {
    User user = new User();
    user.setId(id);
    user.setNickname(nickname);
    return user;
  }

  static class FakeDirectMessageMapper implements DirectMessageMapper {
    @Override
    public int insert(DirectMessage message) {
      message.setId(9L);
      return 1;
    }

    @Override
    public List<DirectMessageVo> findConversation(Long userId, Long otherUserId) {
      return java.util.Collections.emptyList();
    }

    @Override
    public List<MessageThreadVo> findThreads(Long userId) {
      MessageThreadVo hidden = thread(1L, "Hide", "a", "old", LocalDateTime.now().minusHours(2), 0);
      MessageThreadVo visible = thread(3L, "Keep", "b", "new", LocalDateTime.now(), 0);
      return Arrays.asList(hidden, visible);
    }

    @Override
    public Long findLatestMessageId(Long userId, Long otherUserId) {
      return 8L;
    }

    @Override
    public int markConversationRead(Long userId, Long otherUserId) {
      return 0;
    }

    @Override
    public int countUnreadByReceiver(Long userId) {
      return 0;
    }

    private MessageThreadVo thread(Long userId, String nickname, String avatarUrl, String lastMessage, LocalDateTime lastMessageAt, int unreadCount) {
      MessageThreadVo thread = new MessageThreadVo();
      thread.setUserId(userId);
      thread.setNickname(nickname);
      thread.setAvatarUrl(avatarUrl);
      thread.setLastMessage(lastMessage);
      thread.setLastMessageAt(lastMessageAt);
      thread.setUnreadCount(unreadCount);
      return thread;
    }
  }

  static class FakeMessageThreadHideMapper implements MessageThreadHideMapper {
    boolean saved;

    @Override
    public MessageThreadHide findByUserAndPartner(Long userId, Long partnerId) {
      if (saved && partnerId.equals(1L)) {
        MessageThreadHide hide = new MessageThreadHide();
        hide.setUserId(userId);
        hide.setPartnerId(partnerId);
        hide.setHiddenAfterMessageId(8L);
        return hide;
      }
      return null;
    }

    @Override
    public int upsertHide(Long userId, Long partnerId, Long hiddenAfterMessageId) {
      saved = true;
      return 1;
    }

    @Override
    public int deleteByUserAndPartner(Long userId, Long partnerId) {
      return 1;
    }
  }

  static class FakeUserMapper implements UserMapper {
    @Override
    public User findByUsername(String username) {
      return null;
    }

    @Override
    public User findById(Long id) {
      User user = new User();
      user.setId(id);
      user.setNickname("user" + id);
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
  }

  static class FakeNotificationService extends NotificationService {
    FakeNotificationService() {
      super(null, null);
    }
  }
}
