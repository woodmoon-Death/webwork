package com.example.social.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class IpLocationService {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
    if (isLocalAddress(value)) {
      return "本机";
    }
    String location = lookupLocation(value);
    if (isPresent(location)) {
      return location;
    }
    return "未知";
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

  private String lookupLocation(String ip) {
    HttpURLConnection connection = null;
    try {
      URL url = new URL("https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true");
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(3000);
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          body.append(line);
        }
        return parseLocation(body.toString());
      }
    } catch (Exception ignored) {
      return "";
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private String parseLocation(String body) {
    try {
      JsonNode root = OBJECT_MAPPER.readTree(body);
      String province = clean(root.path("pro").asText(""));
      String city = clean(root.path("city").asText(""));
      String region = clean(root.path("region").asText(""));
      String addr = clean(root.path("addr").asText(""));
      String err = clean(root.path("err").asText(""));

      if (isPresent(addr) && !"noprovince".equalsIgnoreCase(err)) {
        String combined = combine(province, city, region);
        if (isPresent(combined)) {
          return combined;
        }
      }
      String combined = combine(province, city, region);
      if (isPresent(combined)) {
        return combined;
      }
      return addr;
    } catch (Exception ignored) {
      return "";
    }
  }

  private String combine(String province, String city, String region) {
    StringBuilder builder = new StringBuilder();
    appendIfMissing(builder, province);
    appendIfMissing(builder, city);
    appendIfMissing(builder, region);
    return builder.toString();
  }

  private void appendIfMissing(StringBuilder builder, String part) {
    if (!isPresent(part)) {
      return;
    }
    String value = clean(part);
    if (!isPresent(value)) {
      return;
    }
    if (builder.indexOf(value) >= 0) {
      return;
    }
    builder.append(value);
  }

  private boolean isLocalAddress(String value) {
    return "0:0:0:0:0:0:0:1".equals(value) || "::1".equals(value) || "127.0.0.1".equals(value)
        || value.startsWith("192.168.") || value.startsWith("10.")
        || value.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*");
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

  private String clean(String value) {
    if (!isPresent(value)) {
      return "";
    }
    String cleaned = value.trim();
    if (cleaned.startsWith("中国")) {
      cleaned = cleaned.substring(2);
    }
    return cleaned;
  }
}
