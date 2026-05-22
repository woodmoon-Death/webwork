package com.example.social;

import com.example.social.dto.MessageRequest;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MessageServiceTest {
  @Test
  void sendReturnsInsertedMessageWithoutReloadingConversation() {
    FakeDirectMessageMapper messageMapper = new FakeDirectMessageMapper();
    User receiver = user(1L, "Forget", "/avatar.png");
    MessageService service = new MessageService(messageMapper, new FakeMessageThreadHideMapper(), new FakeUserMapper(receiver), nullNotificationService());
    User sender = user(2L, "GGB", null);
    MessageRequest request = new MessageRequest();
    request.setReceiverId(receiver.getId());
    request.setContent("  hello  ");

    DirectMessageVo result = service.send(sender, request);

    assertEquals(99L, result.getId());
    assertEquals(sender.getId(), result.getSenderId());
    assertEquals(receiver.getId(), result.getReceiverId());
    assertEquals("hello", result.getContent());
    assertEquals(sender.getNickname(), result.getSenderName());
    assertEquals(receiver.getNickname(), result.getReceiverName());
    assertFalse(result.isRead());
    assertEquals(0, messageMapper.findConversationCalls);
  }

  private static User user(Long id, String nickname, String avatarUrl) {
    User user = new User();
    user.setId(id);
    user.setNickname(nickname);
    user.setAvatarUrl(avatarUrl);
    return user;
  }

  private NotificationService nullNotificationService() {
    return new FakeNotificationService();
  }

  static class FakeDirectMessageMapper implements DirectMessageMapper {
    int findConversationCalls = 0;

    @Override
    public int insert(DirectMessage message) {
      message.setId(99L);
      return 1;
    }

    @Override
    public List<DirectMessageVo> findConversation(Long userId, Long otherUserId) {
      findConversationCalls++;
      throw new AssertionError("send should not reload the full conversation");
    }

    @Override
    public Long findLatestMessageId(Long userId, Long otherUserId) {
      return null;
    }

    @Override
    public List<MessageThreadVo> findThreads(Long userId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int markConversationRead(Long userId, Long otherUserId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int countUnreadByReceiver(Long userId) {
      throw new UnsupportedOperationException();
    }
  }

  static class FakeUserMapper implements UserMapper {
    private final User receiver;

    FakeUserMapper(User receiver) {
      this.receiver = receiver;
    }

    @Override
    public User findByUsername(String username) {
      throw new UnsupportedOperationException();
    }

    @Override
    public User findById(Long id) {
      return receiver.getId().equals(id) ? receiver : null;
    }

    @Override
    public int insert(User user) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int updateProfile(Long id, String nickname, String bio, String avatarUrl) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int countReceivedLikes(Long id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int countUsers() {
      throw new UnsupportedOperationException();
    }
  }

  static class FakeMessageThreadHideMapper implements MessageThreadHideMapper {
    @Override
    public MessageThreadHide findByUserAndPartner(Long userId, Long partnerId) {
      return null;
    }

    @Override
    public int upsertHide(Long userId, Long partnerId, Long hiddenAfterMessageId) {
      return 1;
    }

    @Override
    public int deleteByUserAndPartner(Long userId, Long partnerId) {
      return 1;
    }
  }

  static class FakeNotificationService extends NotificationService {
    FakeNotificationService() {
      super(null, null);
    }

    @Override
    public void notifyMessage(User receiver, User actor, Long messageId) {
    }
  }
}
