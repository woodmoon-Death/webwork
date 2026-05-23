package com.example.social.service;

import com.example.social.entity.FileRecord;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.example.social.util.TimeUtil;

@Service
public class FileService {
  private static final long MAX_SIZE = 5L * 1024L * 1024L;
  private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp"));

  private final FileMapper fileMapper;
  private final Path uploadDirectory;

  public FileService(FileMapper fileMapper, @Value("${app.upload-dir:uploads}") String uploadDir) {
    this.fileMapper = fileMapper;
    this.uploadDirectory = resolveUploadDirectory(uploadDir);
  }

  public FileRecord upload(MultipartFile file, User user) {
    if (file == null || file.isEmpty()) {
      throw new BusinessException("请选择图片");
    }
    if (file.getSize() > MAX_SIZE) {
      throw new BusinessException("图片不能超过 5MB");
    }
    if (!ALLOWED_TYPES.contains(file.getContentType())) {
      throw new BusinessException("仅支持 jpg、png、webp 图片");
    }

    String ext = extension(file.getOriginalFilename());
    String filename = UUID.randomUUID() + ext;
    Path target = uploadDirectory.resolve(filename).normalize();
    try {
      Files.createDirectories(uploadDirectory);
      if (!target.startsWith(uploadDirectory)) {
        throw new BusinessException("文件路径不正确");
      }
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException | SecurityException exception) {
      throw new BusinessException(500, "图片保存失败，请检查上传目录配置");
    }

    FileRecord record = new FileRecord();
    record.setUserId(user.getId());
    record.setOriginalName(file.getOriginalFilename());
    record.setStoragePath(target.toString());
    record.setContentType(file.getContentType());
    record.setSize(file.getSize());
    record.setCreatedAt(TimeUtil.nowBeijing());
    fileMapper.insert(record);
    record.setUrl("/api/files/" + record.getId());
    fileMapper.updateUrl(record.getId(), record.getUrl());
    return record;
  }

  public FileRecord findById(Long id) {
    FileRecord record = fileMapper.findById(id);
    if (record == null) {
      throw new BusinessException(404, "图片不存在");
    }
    return record;
  }

  public Resource loadAsResource(FileRecord record) {
    Path path = Paths.get(record.getStoragePath()).toAbsolutePath().normalize();
    if (!path.startsWith(uploadDirectory)) {
      throw new BusinessException(400, "文件路径不正确");
    }
    FileSystemResource resource = new FileSystemResource(path);
    if (!resource.exists() || !resource.isReadable()) {
      throw new BusinessException(404, "图片文件不存在");
    }
    return resource;
  }

  public MediaType mediaType(FileRecord record) {
    try {
      return MediaType.parseMediaType(record.getContentType());
    } catch (Exception exception) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
  }

  private String extension(String originalName) {
    if (originalName == null || !originalName.contains(".")) {
      return ".jpg";
    }
    return originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
  }

  private Path resolveUploadDirectory(String uploadDir) {
    Path preferred = normalizeUploadDirectory(uploadDir);
    if (isWritableDirectory(preferred)) {
      return preferred;
    }
    Path fallback = Paths.get(System.getProperty("java.io.tmpdir"), "webwork-uploads").toAbsolutePath().normalize();
    if (isWritableDirectory(fallback)) {
      return fallback;
    }
    return preferred;
  }

  private Path normalizeUploadDirectory(String uploadDir) {
    String candidate = uploadDir;
    if (candidate == null || candidate.trim().isEmpty()) {
      candidate = Paths.get(System.getProperty("java.io.tmpdir"), "webwork-uploads").toString();
    }
    return Paths.get(candidate).toAbsolutePath().normalize();
  }

  private boolean isWritableDirectory(Path directory) {
    try {
      Files.createDirectories(directory);
      return Files.isDirectory(directory) && Files.isWritable(directory);
    } catch (IOException exception) {
      return false;
    }
  }
}
