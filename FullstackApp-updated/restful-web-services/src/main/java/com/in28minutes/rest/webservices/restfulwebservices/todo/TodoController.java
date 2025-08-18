package com.in28minutes.rest.webservices.restfulwebservices.todo;

import com.in28minutes.rest.webservices.restfulwebservices.activity.ActivityLogService;
import com.in28minutes.rest.webservices.restfulwebservices.user.User;
import com.in28minutes.rest.webservices.restfulwebservices.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public TodoController(TodoService todoService, UserService userService, ActivityLogService activityLogService) {
        this.todoService = todoService;
        this.userService = userService;
        this.activityLogService = activityLogService;
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
    public Todo retrieveTodo(@PathVariable String username, @PathVariable Long id) {
        return todoService.findById(id);
    }

    // Delete a todo by ID
    @DeleteMapping("/users/{username}/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String username,
                                           @PathVariable Long id,
                                           Authentication authentication) {
        // Log the deletion BEFORE actually deleting (so we still have the todo data)
        activityLogService.logTodoDeletion(authentication.getName(), Long.valueOf(id));

        todoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Update an existing todo
    @PutMapping("/users/{username}/todos/{id}")
    public Todo updateTodo(@PathVariable String username,
                           @PathVariable Long id,
                           @RequestBody Todo todo,
                           Authentication authentication) {

        System.out.println("Update called by: " + authentication.getName() + ", for user: " + username);
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }

        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            throw new TodoNotFoundException("Todo with ID " + id + " not found");
        }

        // Track original values
        String oldAssignedTo = existingTodo.getAssignedTo();
        Todo.TodoStatus originalStatus = existingTodo.getStatus();
        String originalComments = existingTodo.getComments();
        String originalDescription = existingTodo.getDescription();

        // Apply incoming updates
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

        // Logging logic
        boolean statusChanged = todo.getStatus() != null && !originalStatus.equals(todo.getStatus());
        boolean commentAdded = todo.getComments() != null && !todo.getComments().equals(originalComments);
        boolean descChanged = todo.getDescription() != null && !todo.getDescription().equals(originalDescription);
        boolean assignedChanged = todo.getAssignedTo() != null && !oldAssignedTo.equals(todo.getAssignedTo());

        if (statusChanged && originalStatus != Todo.TodoStatus.DONE && todo.getStatus() == Todo.TodoStatus.DONE) {
            activityLogService.logTodoCompletion(authentication.getName(), Long.valueOf(id));
        } else if ("UNASSIGNED".equalsIgnoreCase(oldAssignedTo) && assignedChanged) {
            activityLogService.logTodoAssignment(authentication.getName(), Long.valueOf(id));
        } else if (assignedChanged) {
            activityLogService.logActivity("TODO_REASSIGNED", authentication.getName(), Long.valueOf(id));
        } else if (statusChanged) {
            activityLogService.logActivity("TODO_STATUS_CHANGED", authentication.getName(), Long.valueOf(id));
        } else if (commentAdded) {
            activityLogService.logActivity("TODO_COMMENT_ADDED", authentication.getName(), Long.valueOf(id));
        } else if (descChanged) {
            activityLogService.logActivity("TODO_DESCRIPTION_UPDATED", authentication.getName(), Long.valueOf(id));
        } else {
            activityLogService.logTodoUpdate(authentication.getName(), Long.valueOf(id));
        }

        return existingTodo;
    }


    @PostMapping ("/users/{username}/todos")
    public Todo createTodo(@PathVariable String username,
                           @RequestBody Todo todo,
                           Authentication authentication) {
        // Ensure new todos have no ID
        todo.setId(null);

        // Set default status if not provided
        if (todo.getStatus() == null) {
            todo.setStatus(Todo.TodoStatus.TODO);
        }

        // Handle assignment: default to UNASSIGNED if not provided
        String assignedTo = (todo.getAssignedTo() == null || todo.getAssignedTo().isBlank())
                ? "UNASSIGNED"
                : todo.getAssignedTo();

        // Create the todo
        Todo createdTodo = todoService.addTodo(username, todo.getDescription(), todo.getTargetDate(), todo.getComments(), assignedTo);

        // Log creation
        activityLogService.logTodoCreation(authentication.getName(), Long.valueOf(createdTodo.getId()));

        // Log assignment if not UNASSIGNED
        if (!"UNASSIGNED".equalsIgnoreCase(createdTodo.getAssignedTo())) {
            activityLogService.logTodoAssignment(authentication.getName(), Long.valueOf(createdTodo.getId()));
        }

        return createdTodo;
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers(); // Call the UserService to fetch all users
    }

    // Update Todo assignment
    @PutMapping("/users/{username}/todos/{id}/assign")
    public Todo updateTodoAssignment(@PathVariable String username,
                                     @PathVariable Long id,
                                     @RequestParam String assignedTo,
                                     Authentication authentication) {
        Todo todo = todoService.findById(id);
        String oldAssignedTo = todo.getAssignedTo();
        todo.setAssignedTo(assignedTo);
        todoService.updateTodo(todo);

        if ("UNASSIGNED".equalsIgnoreCase(oldAssignedTo)) {
            activityLogService.logTodoAssignment(authentication.getName(), Long.valueOf(id));
        } else if (!oldAssignedTo.equals(assignedTo)) {
            activityLogService.logActivity("TODO_REASSIGNED", authentication.getName(), Long.valueOf(id));
        }

        return todo;
    }
}