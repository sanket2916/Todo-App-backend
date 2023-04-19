package com.kaneki.springboot.todo_list;

import com.kaneki.springboot.todo_list.entity.Task;
import com.kaneki.springboot.todo_list.entity.User;
import com.kaneki.springboot.todo_list.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootApplication
public class TodoListApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoListApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserService userService) {
		LocalDateTime dateTime = LocalDateTime.now();
		Date date = Date.valueOf(LocalDate.now());
		return args -> {
			userService.saveUser(new User(0, "Random", "1", "random1",
					"random1@gmail.com", "todo", new ArrayList<>()));
			userService.addTask(1, new Task(0, "Code", false, "Work", date));
		};
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

}
