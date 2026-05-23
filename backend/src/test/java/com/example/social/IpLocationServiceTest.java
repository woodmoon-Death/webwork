package com.example.social;

import com.example.social.service.IpLocationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpLocationServiceTest {
  private final IpLocationService service = new IpLocationService();

  @Test
  void displayLocationClassifiesLocalLanAndPublicAddresses() {
    assertEquals("本机", service.displayLocation("127.0.0.1"));
    assertEquals("本机", service.displayLocation("192.168.1.8"));
    assertEquals("本机", service.displayLocation("172.20.2.4"));
    assertEquals("未知", service.displayLocation("8.8.8.8"));
  }

  @Test
  void maskIpHidesPreciseIpv4Address() {
    assertEquals("8.8.*.*", service.maskIp("8.8.8.8"));
  }
}
