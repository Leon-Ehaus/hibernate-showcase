package com.example.hibernateshowcase.services;

import com.example.hibernateshowcase.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EntityStatesTest {

  @Autowired
  UserService userService;
  @Autowired
  UserRepository userRepository;
  @Autowired
  EntityManager entityManager;

  private Statistics getStatistics() {
    return entityManager.unwrap(Session.class).getSessionFactory().getStatistics();
  }

  @BeforeEach
  void init() {
    userRepository.deleteAll();
    getStatistics().clear();
  }

  @Test
  public void test_create_user() {
    userService.saveNewUser("test");
    assertThat(userRepository.findAll())
      .singleElement()
      .satisfies(user -> assertThat(user.getName()).isEqualTo("test"));
  }

}