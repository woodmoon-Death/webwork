package com.example.social.mapper;

import com.example.social.entity.Post;
import com.example.social.vo.PostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
  int insert(Post post);

  int update(Post post);

  int deleteById(@Param("id") Long id);

  Post findEntityById(@Param("id") Long id);

  PostVo findVoById(@Param("id") Long id, @Param("viewerId") Long viewerId, @Param("includeHidden") boolean includeHidden);

  List<PostVo> findFeed(@Param("keyword") String keyword, @Param("filter") String filter, @Param("viewerId") Long viewerId, @Param("includeHidden") boolean includeHidden);

  List<PostVo> findByUserId(@Param("userId") Long userId, @Param("viewerId") Long viewerId);

  int updateVisibility(@Param("id") Long id, @Param("visibility") String visibility);

  int countPosts();

  int countHiddenPosts();
}
