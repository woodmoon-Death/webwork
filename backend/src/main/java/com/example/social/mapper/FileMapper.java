package com.example.social.mapper;

import com.example.social.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMapper {
  int insert(FileRecord fileRecord);

  FileRecord findById(@Param("id") Long id);

  int updateUrl(@Param("id") Long id, @Param("url") String url);
}
