package com.example.social.mapper;

import com.example.social.entity.DirectMessage;
import com.example.social.vo.DirectMessageVo;
import com.example.social.vo.MessageThreadVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DirectMessageMapper {
  int insert(DirectMessage message);

  List<DirectMessageVo> findConversation(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

  List<MessageThreadVo> findThreads(@Param("userId") Long userId);

  Long findLatestMessageId(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

  int markConversationRead(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

  int countUnreadByReceiver(@Param("userId") Long userId);
}
