package mar.sirenko.task_system;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Long countByAssignedUserIdAndStatus(Long assignedUserId, TaskStatus status);
}
