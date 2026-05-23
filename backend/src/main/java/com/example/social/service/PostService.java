package com.example.social.service;

import com.example.social.dto.PostRequest;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.exception.BusinessException;
import com.example.social.mapper.PostMapper;
import com.example.social.security.Role;
import com.example.social.util.TimeUtil;
import com.example.social.vo.PostVo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {
  private static final Set<String> ALLOWED_TAGS = new LinkedHashSet<>(Arrays.asList("校园日常", "学习记录", "图片分享", "生活灵感", "项目展示"));

  private final PostMapper postMapper;
  private final PermissionService permissionService;
  private final IpLocationService ipLocationService;

  public PostService(PostMapper postMapper, PermissionService permissionService, IpLocationService ipLocationService) {
    this.postMapper = postMapper;
    this.permissionService = permissionService;
    this.ipLocationService = ipLocationService;
  }

  public List<PostVo> feed(String keyword, String filter, User viewer) {
    boolean includeHidden = viewer != null && Role.ADMIN.name().equals(viewer.getRole());
    return postMapper.findFeed(keyword, filter, viewer == null ? null : viewer.getId(), includeHidden);
  }

  public PostVo detail(Long id, User viewer) {
    boolean includeHidden = viewer != null && Role.ADMIN.name().equals(viewer.getRole());
    PostVo post = postMapper.findVoById(id, viewer == null ? null : viewer.getId(), includeHidden);
    if (post == null) {
      throw new BusinessException(404, "分享不存在");
    }
    return post;
  }

  public PostVo create(PostRequest request, User user, HttpServletRequest httpRequest) {
    Post post = new Post();
    String clientIp = ipLocationService.resolveClientIp(httpRequest);
    post.setUserId(user.getId());
    post.setTitle(request.getTitle().trim());
    post.setContent(request.getContent().trim());
    post.setImageUrl(request.getImageUrl());
    post.setTags(normalizeTags(request.getTags()));
    post.setIpLocation(ipLocationService.displayLocation(clientIp));
    post.setIpAddress(ipLocationService.maskIp(clientIp));
    post.setVisibility("PUBLIC");
    post.setCreatedAt(TimeUtil.nowBeijing());
    post.setUpdatedAt(TimeUtil.nowBeijing());
    postMapper.insert(post);
    return detail(post.getId(), user);
  }

  public PostVo update(Long id, PostRequest request, User user) {
    Post post = postMapper.findEntityById(id);
    permissionService.requirePostOwnerOrAdmin(user, post);
    post.setTitle(request.getTitle().trim());
    post.setContent(request.getContent().trim());
    post.setImageUrl(request.getImageUrl());
    post.setTags(normalizeTags(request.getTags()));
    post.setUpdatedAt(TimeUtil.nowBeijing());
    postMapper.update(post);
    return detail(id, user);
  }

  public void delete(Long id, User user) {
    Post post = postMapper.findEntityById(id);
    permissionService.requirePostOwnerOrAdmin(user, post);
    postMapper.deleteById(id);
  }

  public PostVo visibility(Long id, String visibility, User user) {
    if (!Role.ADMIN.name().equals(user.getRole())) {
      throw new BusinessException(403, "需要管理员权限");
    }
    if (!"PUBLIC".equals(visibility) && !"HIDDEN".equals(visibility)) {
      throw new BusinessException("可见状态不正确");
    }
    postMapper.updateVisibility(id, visibility, TimeUtil.nowBeijing());
    return detail(id, user);
  }

  public String normalizeTags(List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return "";
    }
    List<String> normalized = tags.stream()
        .filter(tag -> tag != null && !tag.trim().isEmpty())
        .map(String::trim)
        .distinct()
        .collect(Collectors.toList());
    if (normalized.size() > 3) {
      throw new BusinessException("最多选择 3 个标签");
    }
    for (String tag : normalized) {
      if (!ALLOWED_TAGS.contains(tag)) {
        throw new BusinessException("标签不在可选范围内");
      }
    }
    return String.join(",", normalized);
  }
}
