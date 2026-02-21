package com.example.hibernateshowcase.repositories;

import java.util.UUID;

import com.example.hibernateshowcase.entities.PostEntity;
import com.example.hibernateshowcase.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {
}
