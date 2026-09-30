package mar.sirenko.task_system.web;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {
}
