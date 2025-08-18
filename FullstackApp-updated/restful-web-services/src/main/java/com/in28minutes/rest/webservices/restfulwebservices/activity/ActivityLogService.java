package com.in28minutes.rest.webservices.restfulwebservices.activity;

import com.in28minutes.rest.webservices.restfulwebservices.todo.Todo;
import com.in28minutes.rest.webservices.restfulwebservices.todo.TodoRepository;
import com.in28minutes.rest.webservices.restfulwebservices.user.User;
import com.in28minutes.rest.webservices.restfulwebservices.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository,
                              UserRepository userRepository,
                              TodoRepository todoRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
    }

    public void logActivity(String action, String username, Long todoId) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            Optional<Todo> todoOpt = todoRepository.findById(todoId);


            if (userOpt.isPresent() && todoOpt.isPresent()) {
                ActivityLog log = new ActivityLog();
                log.setAction(action);
                log.setUser(userOpt.get());
                log.setTodo(todoOpt.get());
                log.setTimestamp(LocalDateTime.now());

                activityLogRepository.save(log);
            }
        } catch (Exception e) {
            // Log the error but don't fail the main operation
            System.err.println("Failed to create activity log: " + e.getMessage());
        }
    }

    public void logTodoCreation(String username, Long todoId) {
        logActivity("TODO_CREATED", username, todoId);
    }

    public void logTodoUpdate(String username, Long todoId) {
        logActivity("TODO_UPDATED", username, todoId);
    }

    public void logTodoCompletion(String username, Long todoId) {
        logActivity("TODO_COMPLETED", username, todoId);
    }

    public void logTodoDeletion(String username, Long todoId) {
        logActivity("TODO_DELETED", username, todoId);
    }

    public void logTodoAssignment(String username, Long todoId) {
        logActivity("TODO_ASSIGNED", username, todoId);
    }

    // Get methods for the controller to use
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }

    public List<ActivityLog> getLogsByUser(String username) {
        return activityLogRepository.findByUser(username);
    }

    public List<ActivityLog> getLogsByTodo(Long todoId) {
        return activityLogRepository.findByTodoId(todoId);
    }
}