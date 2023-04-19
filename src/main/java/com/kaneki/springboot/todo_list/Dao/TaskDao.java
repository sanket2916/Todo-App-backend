package com.kaneki.springboot.todo_list.Dao;

import com.kaneki.springboot.todo_list.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDao extends JpaRepository<Task, Integer> {

}
