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
class NPlusOneTest {

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
  public void test_n_plus_one() {
    var user1 = userService.saveNewUser("user1");
    var user2 = userService.saveNewUser("user2");
    var user3 = userService.saveNewUser("user3");

    userService.addPostToUser(user1.getId(), "post1");
    userService.addPostToUser(user1.getId(), "post2");
    userService.addPostToUser(user2.getId(), "post3");
    userService.addPostToUser(user3.getId(), "post4");

    getStatistics().clear();
    var users = userService.getAllUsersAndLogAllTheirPosts();
    assertThat(users).hasSize(3);
    // 1 query to get all users, plus 3 queries to get posts for each user (N+1)
    assertThat(getStatistics().getPrepareStatementCount()).isEqualTo(4);
  }

  @Test
  public void test_join_fetch() {
    var user1 = userService.saveNewUser("user1");
    var user2 = userService.saveNewUser("user2");
    var user3 = userService.saveNewUser("user3");

    userService.addPostToUser(user1.getId(), "post1");
    userService.addPostToUser(user1.getId(), "post2");
    userService.addPostToUser(user2.getId(), "post3");
    userService.addPostToUser(user3.getId(), "post4");

    getStatistics().clear();
    // This uses one query due to join fetch, and posts are already loaded
    var users = userService.getAllUsersAndLogAllTheirPostsJoinFetch();
    assertThat(users).hasSize(3);
    // Only 1 query to get users and their posts
    assertThat(getStatistics().getPrepareStatementCount()).isOne();
  }

  @Test
  public void test_entity_graph() {
    var user1 = userService.saveNewUser("user1");
    var user2 = userService.saveNewUser("user2");
    var user3 = userService.saveNewUser("user3");

    userService.addPostToUser(user1.getId(), "post1");
    userService.addPostToUser(user1.getId(), "post2");
    userService.addPostToUser(user2.getId(), "post3");
    userService.addPostToUser(user3.getId(), "post4");

    getStatistics().clear();
    // This uses one query due to entity graph, and posts are already loaded
    var users = userService.getAllUsersAndLogAllTheirPostsEntityGraph();
    assertThat(users).hasSize(3);
    // Only 1 query to get users and their posts
    assertThat(getStatistics().getPrepareStatementCount()).isOne();
  }

  @Test
  public void test_batch_size() {
    var user1 = userService.saveNewUser("user1");
    var user2 = userService.saveNewUser("user2");
    var user3 = userService.saveNewUser("user3");

    userService.addPostToUser(user1.getId(), "post1");
    userService.addPostToUser(user1.getId(), "post2");
    userService.addPostToUser(user2.getId(), "post3");
    userService.addPostToUser(user3.getId(), "post4");

    getStatistics().clear();
    var users = userService.getAllUsersAndLogAllTheirPosts();
    assertThat(users).hasSize(3);
    // 1 query to get all users, plus 1 queries to get the posts for the first 100 users
    assertThat(getStatistics().getPrepareStatementCount()).isEqualTo(2);
  }

}