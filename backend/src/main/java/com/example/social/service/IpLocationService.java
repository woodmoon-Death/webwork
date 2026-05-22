package com.example.social.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class IpLocationService {
  public String resolveClientIp(HttpServletRequest request) {
    String forwarded = firstHeaderValue(request.getHeader("X-Forwarded-For"));
    if (isPresent(forwarded)) {
      return forwarded;
    }
    String realIp = request.getHeader("X-Real-IP");
    if (isPresent(realIp)) {
      return realIp.trim();
    }
    return request.getRemoteAddr();
  }

  public String displayLocation(String ip) {
    if (!isPresent(ip)) {
      return "未知";
    }
    String value = ip.trim();
    if ("0:0:0:0:0:0:0:1".equals(value) || "::1".equals(value) || "127.0.0.1".equals(value)) {
      return "本机";
    }
    if (value.startsWith("192.168.") || value.startsWith("10.") || value.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*")) {
      return "局域网";
    }
    return "公网";
  }

  public String maskIp(String ip) {
    if (!isPresent(ip)) {
      return "";
    }
    String value = ip.trim();
    if (value.contains(":")) {
      int index = value.indexOf(':');
      return index > 0 ? value.substring(0, index) + ":*" : "::*";
    }
    String[] parts = value.split("\\.");
    if (parts.length != 4) {
      return value;
    }
    return parts[0] + "." + parts[1] + ".*.*";
  }

  private String firstHeaderValue(String header) {
    if (!isPresent(header)) {
      return "";
    }
    return header.split(",")[0].trim();
  }

  private boolean isPresent(String value) {
    return value != null && !value.trim().isEmpty() && !"unknown".equalsIgnoreCase(value.trim());
  }
}
