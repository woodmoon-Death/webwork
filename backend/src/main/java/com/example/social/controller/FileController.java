package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.entity.FileRecord;
import com.example.social.security.CurrentUser;
import com.example.social.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/files")
public class FileController {
  private final FileService fileService;
  private final CurrentUser currentUser;

  public FileController(FileService fileService, CurrentUser currentUser) {
    this.fileService = fileService;
    this.currentUser = currentUser;
  }

  @PostMapping("/upload")
  public ApiResponse<FileRecord> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
    return ApiResponse.success(fileService.upload(file, currentUser.required(request)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Resource> view(@PathVariable Long id) {
    FileRecord record = fileService.findById(id);
    return ResponseEntity.ok()
        .contentType(fileService.mediaType(record))
        .contentLength(record.getSize())
        .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
        .body(fileService.loadAsResource(record));
  }
}
