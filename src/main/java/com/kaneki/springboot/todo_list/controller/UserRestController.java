package com.kaneki.springboot.todo_list.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaneki.springboot.todo_list.entity.Task;
import com.kaneki.springboot.todo_list.entity.User;
import com.kaneki.springboot.todo_list.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private UserService userService;
    HttpServletResponse response;

    @PostMapping("/saveUser")
    public User saveUser(@RequestBody User user) {
        user.setId(0);
        try {
            userService.saveUser(user);
            return user;
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage() + " Please provide a new username");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PostMapping("user/task/{username}")
    public Task saveTask(@PathVariable String username, @RequestBody Task task) {
        task.setId(0);
        User user = userService.getUser(username);
        userService.addTask(user.getId(), task);
        return task;
    }

    @GetMapping("user/task/{username}")
    public List<Task> getTasks(@PathVariable String username) {
        User user = userService.getUser(username);
        return user.getTasks();
    }

    @GetMapping("user/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @PutMapping("user/task/update/{username}/{taskId}")
    public void updateTask(@PathVariable int taskId, @PathVariable String username) {
        User user = userService.getUser(username);
        Task task = userService.getTask(taskId);
        for(Task temp: user.getTasks()) {
            if(temp == task) {
                userService.updateTask(taskId);
                return;
            }
        }
    }

    @PutMapping("/user/update/{username}")
    public User updateUser(@RequestBody User user) {
        User originalUser = userService.getUser(user.getId());
        user.setTasks(originalUser.getTasks());
        userService.saveUser(user);
        return userService.getUser(user.getId());
    }

    @PutMapping("user/task/update/{username}")
    public Task updateTaskName(@RequestBody Task task, @PathVariable String username) {
        int id = task.getId();
        Task originalTask = userService.getTask(id);
        User user = userService.getUser(username);
        for(Task temp: user.getTasks()) {
            if(temp == originalTask) {
                userService.updateTaskName(task);
                break;
            }
        }
        return userService.getTask(task.getId());
    }

    @DeleteMapping("user/task/delete/{username}/{taskId}")
    public void deleteTask(@PathVariable String username, @PathVariable int taskId) {
        try {
            User user = userService.getUser(username);
            Task task = userService.getTask(taskId);
            for (Task temp : user.getTasks()) {
                if (temp == task) {
                    userService.deleteTask(user.getId(), taskId);
                    return;
                }
            }
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @DeleteMapping("user/delete/{username}")
    public void deleteUser(@PathVariable String username) {
        try{
            User user = userService.getUser(username);
            userService.deleteUser(user.getId());
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
            response.setStatus(SC_NOT_FOUND);
        }
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 1000))
                        .withIssuer(request.getRequestURI())
                        .withClaim("roles", new ArrayList<>())
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                response.setHeader("error", "Refresh" + exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "Refresh " + exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
