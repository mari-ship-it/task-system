package mar.sirenko.task_system;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Task (
        Long id,
        Long creatorId,
        Long assignedUserId,
        TaskStatus status,
        LocalDateTime createDateTime,
        LocalDate deadlineDate,
        Priority priority
){

}
