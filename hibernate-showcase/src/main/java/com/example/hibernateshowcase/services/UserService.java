package com.example.hibernateshowcase.services;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.example.hibernateshowcase.entities.PostEntity;
import com.example.hibernateshowcase.entities.UserEntity;
import com.example.hibernateshowcase.repositories.PostRepository;
import com.example.hibernateshowcase.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final EntityManager entityManager;

  @Transactional
  public UserEntity saveNewUser(String name) {
    //create a new transient entity
    var build = UserEntity.builder()
      .name(name)
      .build();

    //after persisting, the entity will be managed by the persistence context
    var user = userRepository.save(build);

    //after leaving the transactional method, the entity will be detached from the persistence context
    return user;
  }

  @Transactional
  public UserEntity updateUserWithoutSaveCall(UUID id, String name) {
    var userEntity = userRepository.findById(id).orElseThrow();
    userEntity.setName(name);
    return userEntity;
  }

  @Transactional(readOnly = true)
  public List<UserEntity> getAllUsersAndLogAllTheirPosts() {
    var all = userRepository.findAll();
    printAllUsersWithPosts(all);
    return all;
  }

  @Transactional(readOnly = true)
  public List<UserEntity> getAllUsersAndLogAllTheirPostsJoinFetch() {
    var all = userRepository.findAllWithPosts();
    printAllUsersWithPosts(all);
    return all;
  }

  @Transactional(readOnly = true)
  public List<UserEntity> getAllUsersAndLogAllTheirPostsEntityGraph() {
    var all = userRepository.findAllWithPostsUsingEntityGraph();
    printAllUsersWithPosts(all);
    return all;
  }

  @Transactional(readOnly = true)
  public void printAllUsersWithPosts(List<UserEntity> users) {
    users.stream()
      .map(UserEntity::getPosts)
      .flatMap(Collection::stream)
      .forEach(post -> log.info("Post title: {}", post.getTitle()));
  }

  @Transactional
  public void addPostToUser(UUID userId, String postTitle) {
    var user = userRepository.findById(userId).orElseThrow();
    var post = PostEntity.builder()
      .title(postTitle)
      .author(user)
      .build();
    postRepository.save(post);
  }

  @Transactional
  public UserEntity getUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow();
  }
}
