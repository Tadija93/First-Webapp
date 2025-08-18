package com.in28minutes.rest.webservices.restfulwebservices.activity;

import com.in28minutes.rest.webservices.restfulwebservices.todo.Todo;
import com.in28minutes.rest.webservices.restfulwebservices.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;


@Entity
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // e.g., "Task Created", "Marked Done", etc.
    private LocalDateTime timestamp;


    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // Hibernate-specific
    private Todo todo;

    @ManyToOne
    private User user; // who made the change

    public ActivityLog() {
        // Required by JPA and for manual creation via setters
    }

    // Constructors, getters, setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ActivityLog(Long id, String action, LocalDateTime timestamp, Todo todo, User user) {
        this.id = id;
        this.action = action;
        this.timestamp = timestamp;
        this.todo = todo;
        this.user = user;
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", todo=" + todo +
                ", user=" + user +
                '}';
    }
}
