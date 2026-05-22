package com.example.social.mapper;

import com.example.social.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
  int insert(FileRecord fileRecord);
}
