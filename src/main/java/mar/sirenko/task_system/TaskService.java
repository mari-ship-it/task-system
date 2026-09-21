package mar.sirenko.task_system;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class TaskService {

    private final Map<Long, Task> tasksMap = Map.of(
            1L, new Task(
                    1L,
                    1L,
                    2L,
                    TaskStatus.CREATED,
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(3),
                    Priority.MEDIUM
            ),
            2L, new Task(
                    2L,
                    1L,
                    3L,
                    TaskStatus.DONE,
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(3),
                    Priority.HIGH
            ),
            3L, new Task(
                    3L,
                    4L,
                    1L,
                    TaskStatus.DONE,
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(3),
                    Priority.LOW
            ),
            4L, new Task(
                    4L,
                    3L,
                    4L,
                    TaskStatus.IN_PROGRESS,
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(3),
                    Priority.HIGH
            )
    );

    public Task findTaskById(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id: id = " + id);
        }
        return tasksMap.get(id);
    }

    public List<Task> getAllTasks() {
        return tasksMap.values().stream().toList();
    }
}
