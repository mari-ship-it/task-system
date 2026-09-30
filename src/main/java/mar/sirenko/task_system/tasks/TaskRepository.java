package mar.sirenko.task_system.tasks;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query(value = """
                SELECT COUNT(*) FROM (
                SELECT 1 FROM tasks 
                WHERE assigned_user_id = :userId AND status = 'IN_PROGRESS' 
                LIMIT 5
                ) as subquery
                """, nativeQuery = true)
    int countActiveTasksWithLimit(@Param("userId") Long userId);

    @Query("""
            SELECT t FROM TaskEntity t
            WHERE (:creatorId IS NULL OR t.creatorId = :creatorId)
            AND (:assignedUserId IS NULL OR t.assignedUserId = :assignedUserId)
            AND (:status IS NULL OR t.status = :status)
            AND (:priority IS NULL OR t.priority = :priority)
            """)
    List<TaskEntity> searchAllByFilter(
            @Param("creatorId") Long creatorId,
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            Pageable pageable
    );
}
