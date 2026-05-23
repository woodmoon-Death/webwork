package com.example.social.service;

import com.example.social.dto.MessageRequest;
import com.example.social.entity.DirectMessage;
import com.example.social.entity.MessageThreadHide;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.mapper.DirectMessageMapper;
import com.example.social.mapper.MessageThreadHideMapper;
import com.example.social.mapper.UserMapper;
import com.example.social.vo.DirectMessageVo;
import com.example.social.vo.MessageThreadVo;
import com.example.social.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
  private final DirectMessageMapper directMessageMapper;
  private final MessageThreadHideMapper messageThreadHideMapper;
  private final UserMapper userMapper;
  private final NotificationService notificationService;

  public MessageService(DirectMessageMapper directMessageMapper, MessageThreadHideMapper messageThreadHideMapper, UserMapper userMapper, NotificationService notificationService) {
    this.directMessageMapper = directMessageMapper;
    this.messageThreadHideMapper = messageThreadHideMapper;
    this.userMapper = userMapper;
    this.notificationService = notificationService;
  }

  public List<MessageThreadVo> threads(User user) {
    List<MessageThreadVo> threads = directMessageMapper.findThreads(user.getId());
    return threads.stream()
        .filter(thread -> {
          MessageThreadHide hide = messageThreadHideMapper.findByUserAndPartner(user.getId(), thread.getUserId());
          return hide == null || hide.getHiddenAfterMessageId() == null || thread.getLastMessageId() == null || thread.getLastMessageId() > hide.getHiddenAfterMessageId();
        })
        .collect(java.util.stream.Collectors.toList());
  }

  public List<DirectMessageVo> conversation(User user, Long otherUserId) {
    requireOtherUser(otherUserId, user);
    directMessageMapper.markConversationRead(user.getId(), otherUserId);
    notificationService.markMessageNotificationsRead(user, otherUserId);
    return directMessageMapper.findConversation(user.getId(), otherUserId);
  }

  public DirectMessageVo send(User sender, MessageRequest request) {
    User receiver = requireOtherUser(request.getReceiverId(), sender);
    DirectMessage message = new DirectMessage();
    message.setSenderId(sender.getId());
    message.setReceiverId(receiver.getId());
    message.setContent(request.getContent().trim());
    message.setCreatedAt(TimeUtil.nowBeijing());
    directMessageMapper.insert(message);
    messageThreadHideMapper.deleteByUserAndPartner(sender.getId(), receiver.getId());
    messageThreadHideMapper.deleteByUserAndPartner(receiver.getId(), sender.getId());
    notificationService.notifyMessage(receiver, sender, message.getId());
    return toVo(message, sender, receiver);
  }

  public void hideThread(User user, Long otherUserId) {
    User otherUser = requireOtherUser(otherUserId, user);
    Long latestMessageId = directMessageMapper.findLatestMessageId(user.getId(), otherUser.getId());
    if (latestMessageId == null) {
      latestMessageId = 0L;
    }
    messageThreadHideMapper.upsertHide(user.getId(), otherUser.getId(), latestMessageId);
  }

  private DirectMessageVo toVo(DirectMessage message, User sender, User receiver) {
    DirectMessageVo vo = new DirectMessageVo();
    vo.setId(message.getId());
    vo.setSenderId(sender.getId());
    vo.setSenderName(sender.getNickname());
    vo.setSenderAvatarUrl(sender.getAvatarUrl());
    vo.setReceiverId(receiver.getId());
    vo.setReceiverName(receiver.getNickname());
    vo.setReceiverAvatarUrl(receiver.getAvatarUrl());
    vo.setContent(message.getContent());
    vo.setMine(true);
    vo.setRead(false);
    vo.setCreatedAt(message.getCreatedAt());
    return vo;
  }

  private User requireOtherUser(Long otherUserId, User currentUser) {
    if (otherUserId == null) {
      throw new BusinessException(400, "请选择私信对象");
    }
    if (otherUserId.equals(currentUser.getId())) {
      throw new BusinessException(400, "不能给自己发送私信");
    }
    User otherUser = userMapper.findById(otherUserId);
    if (otherUser == null) {
      throw new BusinessException(404, "用户不存在");
    }
    return otherUser;
  }
}
