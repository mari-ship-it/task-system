package mar.sirenko.task_system.tasks;

public record TaskSearchFilter(

        Long creatorId,
        Long assignedUserId,
        TaskStatus status,
        TaskPriority priority,
        int pageSize,
        int pageNumber
        ) {
}
