package com.example.social.mapper;

import com.example.social.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
  User findByUsername(@Param("username") String username);

  User findById(@Param("id") Long id);

  int insert(User user);

  int updateProfile(@Param("id") Long id, @Param("nickname") String nickname, @Param("bio") String bio, @Param("avatarUrl") String avatarUrl);

  int countReceivedLikes(@Param("id") Long id);

  int countUsers();
}
