package com.in28minutes.rest.webservices.restfulwebservices.todo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @NotBlank(message = "Description is required")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "target_date")
    @NotNull(message = "Target date is required")
    private Date targetDate;

    private String assignedTo; // Username of the assigned user

    @Enumerated(EnumType.STRING)
    private TodoStatus status; // This field represents the status

    // New field for comments
    private String comments;

    public Todo() {}

    // Updated constructor including comments
    public Todo(Long id, String username, String description, Date targetDate, TodoStatus status, String comments) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.targetDate = targetDate;
        this.status = status;
        this.comments = comments;
    }

    // Getters and Setters for existing fields ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    // Getter and Setter for comments
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Todo [id=" + id
                + ", username=" + username
                + ", description=" + description
                + ", targetDate=" + targetDate
                + ", status=" + status
                + ", assignedTo=" + assignedTo
                + ", comments=" + comments + "]";
    }

    public enum TodoStatus {
        TODO, IN_PROGRESS, READY_TO_TEST, DONE
    }
}
