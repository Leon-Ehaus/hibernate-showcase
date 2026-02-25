package com.example.hibernateshowcase.services;

import java.util.List;

import com.example.hibernateshowcase.repositories.PostRepository;
import com.example.hibernateshowcase.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LazyLoadingTest {

  @Autowired
  UserService userService;
  @Autowired
  UserRepository userRepository;
  @Autowired
  PostRepository postRepository;
  @Autowired
  EntityManager entityManager;

  private Statistics getStatistics() {
    return entityManager.unwrap(Session.class).getSessionFactory().getStatistics();
  }

  @BeforeEach
  void init() {
    postRepository.deleteAll();
    userRepository.deleteAll();
    getStatistics().clear();
  }

  @Test
  public void test_lazy_loading_exception() {
    var user = userService.saveNewUser("user");

    userService.addPostToUser(user.getId(), "post1");
    userService.addPostToUser(user.getId(), "post2");

    var newJavaObject = userService.getUserById(user.getId());
    assertThatThrownBy(() -> newJavaObject.getPosts()
      .getFirst()
      .getTitle()).isInstanceOf(LazyInitializationException.class);
  }

  @Test
  public void test_lazy_loading_exception_transient_object() {
    var user = userService.saveNewUser("user");

    userService.addPostToUser(user.getId(), "post1");
    userService.addPostToUser(user.getId(), "post2");

    var userEntity = userRepository.findById(user.getId()).orElseThrow();

    assertThatThrownBy(() -> userService.printAllUsersWithPosts(List.of(userEntity))).isInstanceOf(
      LazyInitializationException.class);
  }

}