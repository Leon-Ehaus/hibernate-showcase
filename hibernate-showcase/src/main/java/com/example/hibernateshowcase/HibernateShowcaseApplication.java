package com.example.hibernateshowcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class HibernateShowcaseApplication {

  public static void main(String[] args) {
    SpringApplication.run(HibernateShowcaseApplication.class, args);
  }

}
