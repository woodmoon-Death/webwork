package com.example.social.mapper;

import com.example.social.entity.Notification;
import com.example.social.vo.NotificationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
  int insert(Notification notification);

  List<NotificationVo> findByUserId(@Param("userId") Long userId);

  int countUnreadByUserId(@Param("userId") Long userId);

  int markAllRead(@Param("userId") Long userId);

  int markMessageNotificationsRead(@Param("userId") Long userId, @Param("actorId") Long actorId);
}
