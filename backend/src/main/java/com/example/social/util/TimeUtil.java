package com.example.social.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class TimeUtil {
  public static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

  private TimeUtil() {
  }

  public static LocalDateTime nowBeijing() {
    return LocalDateTime.now(BEIJING_ZONE);
  }
}
