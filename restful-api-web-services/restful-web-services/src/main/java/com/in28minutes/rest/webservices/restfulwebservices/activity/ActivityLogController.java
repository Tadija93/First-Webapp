package com.in28minutes.rest.webservices.restfulwebservices.activity;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activity")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    // Get all activity logs
    @GetMapping
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogService.getAllActivityLogs();
    }

    // Get activity logs for a specific user with optional date filtering
    @GetMapping("/user/{username}")
    public List<ActivityLog> getLogsByUser(@PathVariable String username,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to) {
        List<ActivityLog> logs = activityLogService.getLogsByUser(username);

        // Apply date filtering if provided
        if (from != null && to != null) {
            LocalDateTime fromDate = LocalDateTime.parse(from + "T00:00:00");
            LocalDateTime toDate = LocalDateTime.parse(to + "T23:59:59");

            logs = logs.stream()
                    .filter(log -> log.getTimestamp().isAfter(fromDate) || log.getTimestamp().equals(fromDate))
                    .filter(log -> log.getTimestamp().isBefore(toDate) || log.getTimestamp().equals(toDate))
                    .collect(Collectors.toList());
        }

        return logs;
    }

    // Get activity logs for a specific Todo item
    @GetMapping("/todo/{todoId}")
    public List<ActivityLog> getLogsByTodo(@PathVariable Long todoId) {
        return activityLogService.getLogsByTodo(todoId);
    }
}