package com.kaneki.springboot.todo_list.service;

import com.kaneki.springboot.todo_list.entity.Task;
import com.kaneki.springboot.todo_list.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    void saveUser(User user);
    void addTask(int userId, Task task);
    List<Task> showTasks(int userId);
    void deleteTask(int userId, int taskId);
    void updateTask(int taskId);
    void deleteUser(int id);
    User getUser(int userId);
    Task getTask(int taskId);
    void updateTaskName(Task task);
    User getUser(String username);
}
