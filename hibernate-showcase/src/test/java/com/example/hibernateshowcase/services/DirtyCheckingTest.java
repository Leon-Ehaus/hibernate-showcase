package com.example.hibernateshowcase.services;

import com.example.hibernateshowcase.repositories.PostRepository;
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
class DirtyCheckingTest {

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
  public void test_updateUserWithoutSaveCall() {
    var test = userService.saveNewUser("test");
    assertThat(userRepository.findAll())
      .singleElement()
      .satisfies(user -> assertThat(user.getName()).isEqualTo("test"));
    userService.updateUserWithoutSaveCall(test.getId(), "updated");
    assertThat(userRepository.findAll())
      .singleElement()
      .satisfies(user -> assertThat(user.getName()).isEqualTo("updated"));
  }
  @Test
  public void test_flush_on_select(){
    var user = userService.saveNewUser("test");
    userService.addPostToUser(user.getId(), "post");
    var post = postRepository.findAll().getFirst();
    userService.updatePostAndReturnUser(post.getId(), user.getId(), "updated");
  }

}