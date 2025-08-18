package com.in28minutes.rest.webservices.restfulwebservices.todo;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    // Constructor-based Dependency Injection
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // Retrieve all todos for a specific user
    public List<Todo> findByUsername(String username) {
      List<Todo> todos = todoRepository.findByUsername(username);
     return todos;
    }


    // Retrieve a single todo by its ID
    public Todo findById(int id) {
        return todoRepository.findById((int) id)
                .orElseThrow(() -> new TodoNotFoundException("Todo with ID " + id + " not found"));
    }


    // Delete a todo by its ID
    public void deleteById(int id) {
        todoRepository.deleteById(id);
    }

    // Update an existing todo
    public void updateTodo(Todo todo) {
        todoRepository.save(todo); // Save method handles both create and update
    }

    // Create a new todo
    public Todo addTodo(String username, String description, Date targetDate, String comments) {
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
        todo.setComments(comments); // Set the new comments field
        todo.setAssignedTo("UNASSIGNED" ); // Default value
        todo.setStatus(Todo.TodoStatus.TODO); // Default value

        return todoRepository.save(todo);
    }



    public List<Todo> findByAssignedTo(String assignedTo) {
        List<Todo> todos = todoRepository.findByAssignedTo(assignedTo);
        return todos;
    }
}
