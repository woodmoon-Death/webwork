package com.example.social;

import com.example.social.entity.FileRecord;
import com.example.social.entity.User;
import com.example.social.mapper.FileMapper;
import com.example.social.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileServiceTest {
  @TempDir
  Path tempDir;

  @Test
  void uploadCopiesImageToConfiguredDirectoryAndReturnsPublicUrl() throws Exception {
    FakeFileMapper mapper = new FakeFileMapper();
    FileService service = new FileService(mapper, tempDir.toString());
    User user = new User();
    user.setId(9L);
    MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    FileRecord record = service.upload(file, user);

    assertNotNull(record.getUrl());
    assertTrue(record.getUrl().startsWith("/api/files/"));
    assertTrue(Files.exists(Path.of(record.getStoragePath())));
    assertNotNull(mapper.saved);
  }

  static class FakeFileMapper implements FileMapper {
    FileRecord saved;

    @Override
    public int insert(FileRecord fileRecord) {
      fileRecord.setId(1L);
      saved = fileRecord;
      return 1;
    }

    @Override
    public FileRecord findById(Long id) {
      return saved;
    }
  }
}
