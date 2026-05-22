package com.example.social;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.social.mapper")
public class SocialApplication {
  public static void main(String[] args) {
    SpringApplication.run(SocialApplication.class, args);
  }
}
