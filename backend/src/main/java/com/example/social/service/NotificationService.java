package com.example.social.service;

import com.example.social.entity.Notification;
import com.example.social.entity.User;
import com.example.social.mapper.DirectMessageMapper;
import com.example.social.mapper.NotificationMapper;
import com.example.social.vo.NotificationSummaryVo;
import com.example.social.vo.NotificationVo;
import com.example.social.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
  public static final String TYPE_LIKE = "LIKE";
  public static final String TYPE_COMMENT = "COMMENT";
  public static final String TYPE_MESSAGE = "MESSAGE";

  private final NotificationMapper notificationMapper;
  private final DirectMessageMapper directMessageMapper;

  public NotificationService(NotificationMapper notificationMapper, DirectMessageMapper directMessageMapper) {
    this.notificationMapper = notificationMapper;
    this.directMessageMapper = directMessageMapper;
  }

  public List<NotificationVo> list(User user) {
    return notificationMapper.findByUserId(user.getId()).stream()
        .filter(notification -> !TYPE_MESSAGE.equals(notification.getType()))
        .collect(java.util.stream.Collectors.toList());
  }

  public NotificationSummaryVo summary(User user) {
    return new NotificationSummaryVo(
        notificationMapper.countUnreadByUserId(user.getId()) - directMessageMapper.countUnreadByReceiver(user.getId()),
        directMessageMapper.countUnreadByReceiver(user.getId())
    );
  }

  public void markAllRead(User user) {
    notificationMapper.markAllRead(user.getId());
  }

  public void markMessageNotificationsRead(User user, Long actorId) {
    notificationMapper.markMessageNotificationsRead(user.getId(), actorId);
  }

  public void notifyLike(User receiver, User actor, Long postId, String postTitle) {
    if (receiver == null || actor == null || receiver.getId().equals(actor.getId())) {
      return;
    }
    create(receiver.getId(), actor.getId(), TYPE_LIKE, postId, null, null, actor.getNickname() + " 赞了你的分享《" + postTitle + "》");
  }

  public void notifyComment(User receiver, User actor, Long postId, Long commentId, String postTitle) {
    if (receiver == null || actor == null || receiver.getId().equals(actor.getId())) {
      return;
    }
    create(receiver.getId(), actor.getId(), TYPE_COMMENT, postId, commentId, null, actor.getNickname() + " 评论了你的分享《" + postTitle + "》");
  }

  public void notifyMessage(User receiver, User actor, Long messageId) {
    if (receiver == null || actor == null || receiver.getId().equals(actor.getId())) {
      return;
    }
    create(receiver.getId(), actor.getId(), TYPE_MESSAGE, null, null, messageId, actor.getNickname() + " 给你发来了一条私信");
  }

  private void create(Long userId, Long actorId, String type, Long postId, Long commentId, Long messageId, String content) {
    Notification notification = new Notification();
    notification.setUserId(userId);
    notification.setActorId(actorId);
    notification.setType(type);
    notification.setPostId(postId);
    notification.setCommentId(commentId);
    notification.setMessageId(messageId);
    notification.setContent(content);
    notification.setIsRead(false);
    notification.setCreatedAt(TimeUtil.nowBeijing());
    notificationMapper.insert(notification);
  }
}
