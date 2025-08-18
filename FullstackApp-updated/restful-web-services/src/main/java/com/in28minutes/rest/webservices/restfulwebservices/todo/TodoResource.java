package com.in28minutes.rest.webservices.restfulwebservices.todo;

import com.in28minutes.rest.webservices.restfulwebservices.user.User;
import com.in28minutes.rest.webservices.restfulwebservices.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TodoResource {

    private final TodoService todoService;
    private final UserService userService; // Add UserService to fetch users

    public TodoResource(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    // Retrieve all todos assigned to a specific user
    @GetMapping("/users/{username}/todos")
    public List<Todo> retrieveTodos(@PathVariable String username, Authentication authentication) {
        if ("admin".equals(username)) {
            return todoService.findByUsername(username); // Admin gets all todos
        } else {
            return todoService.findByAssignedTo(username); // User gets only assigned todos
        }
    }

    // Retrieve a specific todo by its ID
    @GetMapping("/users/{username}/todos/{id}")
    public Todo retrieveTodo(@PathVariable String username, @PathVariable int id) {
        return todoService.findById(id);
    }

    // Delete a todo by ID
    @DeleteMapping("/users/{username}/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String username, @PathVariable int id) {
        todoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Update an existing todo
    @PutMapping("/users/{username}/todos/{id}")
    public Todo updateTodo(@PathVariable String username, @PathVariable int id, @RequestBody Todo todo) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }

        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            throw new TodoNotFoundException("Todo with ID " + id + " not found");
        }

        // Update fields
        if (todo.getDescription() != null) {
            existingTodo.setDescription(todo.getDescription());
        }
        if (todo.getTargetDate() != null) {
            existingTodo.setTargetDate(todo.getTargetDate());
        }
        if (todo.getStatus() != null) {
            existingTodo.setStatus(todo.getStatus());
        }
        if (todo.getAssignedTo() != null) {
            existingTodo.setAssignedTo(todo.getAssignedTo());
        }
        if (todo.getComments() != null) {
            existingTodo.setComments(todo.getComments());
        }

        todoService.updateTodo(existingTodo);
        return existingTodo;
    }



    @PostMapping("/users/{username}/todos")
    public Todo createTodo(@PathVariable String username, @RequestBody Todo todo) {
        // Ensure new todos have no ID
        todo.setId(null);

        // Set default status if not provided
        if (todo.getStatus() == null) {
            todo.setStatus(Todo.TodoStatus.TODO);
        }

        // Add and save the todo
        return todoService.addTodo(username, todo.getDescription(), todo.getTargetDate(), todo.getComments());    }



    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers(); // Call the UserService to fetch all users
    }

    // Update Todo assignment
    @PutMapping("/users/{username}/todos/{id}/assign")
    public Todo updateTodoAssignment(@PathVariable String username, @PathVariable int id, @RequestParam String assignedTo) {
        Todo todo = todoService.findById(id);
        todo.setAssignedTo(assignedTo); // Update the assigned user
        todoService.updateTodo(todo); // Save the updated todo
        return todo;
    }
}

