    package com.in28minutes.rest.webservices.restfulwebservices.activity;

    import jakarta.transaction.Transactional;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.time.LocalDateTime;
    import java.util.List;

    public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

        List<ActivityLog> findByTodoId(Long todoId);

        @Query("SELECT a FROM ActivityLog a WHERE a.user.username = :username ORDER BY a.timestamp")
        List<ActivityLog> findByUser(@Param("username") String username);

        @Transactional
        @Modifying
        @Query("DELETE FROM ActivityLog a WHERE a.todo.id = :todoId")
        void deleteByTodoId(@Param("todoId") Long todoId);
    }
