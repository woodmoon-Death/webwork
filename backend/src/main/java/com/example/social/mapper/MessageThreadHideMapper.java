package com.example.social.mapper;

import com.example.social.entity.MessageThreadHide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageThreadHideMapper {
  MessageThreadHide findByUserAndPartner(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

  int upsertHide(@Param("userId") Long userId, @Param("partnerId") Long partnerId, @Param("hiddenAfterMessageId") Long hiddenAfterMessageId);

  int deleteByUserAndPartner(@Param("userId") Long userId, @Param("partnerId") Long partnerId);
}
