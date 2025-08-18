package com.in28minutes.rest.webservices.restfulwebservices.todo;

import com.in28minutes.rest.webservices.restfulwebservices.activity.ActivityLog;
import com.in28minutes.rest.webservices.restfulwebservices.activity.ActivityLogRepository;
import com.in28minutes.rest.webservices.restfulwebservices.user.User;
import com.in28minutes.rest.webservices.restfulwebservices.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    // Constructor-based Dependency Injection

    public TodoService(TodoRepository todoRepository,
                       ActivityLogRepository activityLogRepository,
                       UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }


    // Retrieve all todos for a specific user
    public List<Todo> findByUsername(String username) {
      List<Todo> todos = todoRepository.findByUsername(username);
     return todos;
    }


    // Retrieve a single todo by its ID
    public Todo findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo with ID " + id + " not found"));
    }


    // Delete a todo by its ID
    public void deleteById(Long id) {
        Optional<Todo> todoOptional = todoRepository.findById(id);

        // First delete logs that reference the todo
        activityLogRepository.deleteByTodoId(id);

        todoOptional.ifPresent(todo -> {
            // Log before deleting
            // logActivity("Deleted task", todo.getUsername(), todo);
        });
        todoRepository.findById(id).ifPresentOrElse(
                t -> System.out.println("Found todo: " + t),
                () -> System.out.println("Todo with ID " + id + " not found")
        );

        todoRepository.deleteById(id);
    }

    // Update an existing todo
    public void updateTodo(Todo updatedTodo) {
        Optional<Todo> existingTodoOpt = todoRepository.findById(Long.valueOf(updatedTodo.getId()));

        if (existingTodoOpt.isEmpty()) {
            throw new TodoNotFoundException("Todo with ID " + updatedTodo.getId() + " not found");
        }

        Todo existingTodo = existingTodoOpt.get();

        // Compare and log field-by-field changes
        if (!existingTodo.getStatus().equals(updatedTodo.getStatus())) {
            logActivity("Changed status from " + existingTodo.getStatus() + " to " + updatedTodo.getStatus(), updatedTodo.getUsername(), updatedTodo);
            existingTodo.setStatus(updatedTodo.getStatus());
        }

        if (!existingTodo.getAssignedTo().equals(updatedTodo.getAssignedTo())) {
            logActivity("Reassigned task from " + existingTodo.getAssignedTo() + " to " + updatedTodo.getAssignedTo(), updatedTodo.getUsername(), updatedTodo);
            existingTodo.setAssignedTo(updatedTodo.getAssignedTo());
        }

        if (!existingTodo.getDescription().equals(updatedTodo.getDescription())) {
            logActivity("Updated description from \"" + existingTodo.getDescription() + "\" to \"" + updatedTodo.getDescription() + "\"", updatedTodo.getUsername(), updatedTodo);
            existingTodo.setDescription(updatedTodo.getDescription());
        }

        if (updatedTodo.getComments() != null && !updatedTodo.getComments().equals(existingTodo.getComments())) {
            logActivity("Added comment: \"" + updatedTodo.getComments() + "\"", updatedTodo.getUsername(), updatedTodo);
            existingTodo.setComments(updatedTodo.getComments());
        }

        // Save the updated existingTodo with the changes
        todoRepository.save(existingTodo);
    }



    // Create a new todo
    public Todo addTodo(String username, String description, Date targetDate, String comments, String assignedTo) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (targetDate == null) {
            throw new IllegalArgumentException("Target date is required");
        }

        Todo todo = new Todo();
        todo.setUsername(username);
        todo.setDescription(description);
        todo.setTargetDate(targetDate);
        todo.setComments(comments);
        todo.setAssignedTo(assignedTo != null ? assignedTo : "UNASSIGNED");
        todo.setStatus(Todo.TodoStatus.TODO);

        Todo savedTodo = todoRepository.save(todo);
        logActivity("Created task", username, savedTodo);

        return savedTodo;
    }



    public List<Todo> findByAssignedTo(String assignedTo) {
        List<Todo> todos = todoRepository.findByAssignedTo(assignedTo);
        return todos;
    }



    // Log action into ActivityLog table
    private void logActivity(String action, String username, Todo todo) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setUser(user);
        log.setTodo(todo);
        log.setTimestamp(LocalDateTime.now());

        activityLogRepository.save(log);
    }
}
