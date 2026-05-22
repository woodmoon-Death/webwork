package com.example.social.mapper;

import com.example.social.entity.Comment;
import com.example.social.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
  int insert(Comment comment);

  int deleteById(@Param("id") Long id);

  Comment findEntityById(@Param("id") Long id);

  List<CommentVo> findByPostId(@Param("postId") Long postId);

  int countComments();
}
