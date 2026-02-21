package com.example.hibernateshowcase.repositories;

import java.util.List;
import java.util.UUID;

import com.example.hibernateshowcase.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  @Query("SELECT u FROM UserEntity u JOIN FETCH u.posts")
  List<UserEntity> findAllWithPosts();
}
