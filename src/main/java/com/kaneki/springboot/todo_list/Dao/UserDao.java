package com.kaneki.springboot.todo_list.Dao;

import com.kaneki.springboot.todo_list.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
