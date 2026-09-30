package mar.sirenko.task_system.tasks;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Task (

        @Null
        Long id,

        @NotNull
        Long creatorId,

        Long assignedUserId,
        TaskStatus status,
        LocalDateTime createDateTime,

        @Future
        @NotNull
        LocalDate deadlineDate,

        @NotNull
        TaskPriority priority,
        LocalDateTime doneDateTime
){

}
