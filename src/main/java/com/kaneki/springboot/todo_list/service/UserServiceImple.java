package com.kaneki.springboot.todo_list.service;

import com.kaneki.springboot.todo_list.Dao.TaskDao;
import com.kaneki.springboot.todo_list.Dao.UserDao;
import com.kaneki.springboot.todo_list.entity.Task;
import com.kaneki.springboot.todo_list.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImple implements UserService {
    private TaskDao taskDao;
    private UserDao userDao;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void saveUser(User user) {
        String username = user.getUsername();
        if(userDao.findByUsername(username).isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userDao.save(user);
        } else {
            throw new RuntimeException("username already exists");
        }
    }

    @Override
    public User getUser(String username) {
        var tempUser = userDao.findByUsername(username);
        if(tempUser.isEmpty()) {
            throw new RuntimeException("User does not exists");
        }
        return tempUser.get();
    }

    @Override
    public void addTask(int userId, Task task) {
        taskDao.save(task);
        User user = userDao.getReferenceById(userId);
        user.addTask(task);
        userDao.save(user);
    }

    @Override
    public List<Task> showTasks(int userId) {
        User user = this.getUser(userId);
        return user.getTasks();
    }

    @Override
    public void deleteTask(int userId, int taskId) {
        Task task = this.getTask(taskId);
        User user = this.getUser(userId);
        user.getTasks().remove(task);
        taskDao.delete(task);
        userDao.save(user);
    }

    @Override
    public void updateTask(int taskId) {
        Task task = this.getTask(taskId);
        boolean done = task.isDone();
        task.setDone(!done);
        taskDao.save(task);
    }

    @Override
    public void deleteUser(int userId) throws RuntimeException {
        var user = userDao.findById(userId);
        if(user.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }
        userDao.deleteById(userId);
    }

    @Override
    public User getUser(int userId) throws RuntimeException{
        var user = userDao.findById(userId);
        if(user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return user.get();
    }

//    private Task getTask(int taskId) {
//        var tempTask = taskDao.findById(taskId);
//        if(tempTask.isEmpty()) {
//            throw new RuntimeException("Task Does not exist");
//        }
//        return tempTask.get();
//    }


    @Override
    public Task getTask(int taskId) throws RuntimeException {
        var tempTask = taskDao.findById(taskId);
        if(tempTask.isEmpty()) {
            throw new RuntimeException("Task Does not exist");
        }
        return tempTask.get();
    }

    @Override
    public void updateTaskName(Task task) {
        taskDao.save(task);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var tempUser = userDao.findByUsername(username);
        if(tempUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found in the database");
        }
//        return tempUser.get();
        User user = tempUser.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("USER"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
