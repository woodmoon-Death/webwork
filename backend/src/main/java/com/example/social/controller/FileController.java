package com.example.social.controller;

import com.example.social.common.ApiResponse;
import com.example.social.entity.FileRecord;
import com.example.social.security.CurrentUser;
import com.example.social.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
}
