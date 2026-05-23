package com.example.social.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DateTimeUtil {
  public static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

  private DateTimeUtil() {
  }

  public static ZonedDateTime toBeijing(LocalDateTime dateTime) {
    return dateTime.atZone(BEIJING_ZONE);
  }
}
