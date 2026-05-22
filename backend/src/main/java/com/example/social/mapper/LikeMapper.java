package com.example.social.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {
  int insertIgnore(@Param("postId") Long postId, @Param("userId") Long userId);

  int delete(@Param("postId") Long postId, @Param("userId") Long userId);

  int countByPostId(@Param("postId") Long postId);

  int exists(@Param("postId") Long postId, @Param("userId") Long userId);

  int countLikes();
}
