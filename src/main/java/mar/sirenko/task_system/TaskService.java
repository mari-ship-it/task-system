package mar.sirenko.task_system;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final Map<Long, Task> tasksMap;
    private final AtomicLong idCounter;

    public TaskService() {
        this.tasksMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public Task getTaskById(Long id) {

        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id: id = " + id);
        }
        return tasksMap.get(id);
    }

    public List<Task> findAllTasks() {

        return tasksMap.values().stream().toList();
    }

    public Task createTask(Task createToTask) {

        if (createToTask.id() != null && createToTask.status() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (createToTask.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        Task newTask = new Task(
                idCounter.incrementAndGet(),
                createToTask.creatorId(),
                createToTask.assignedUserId(),
                TaskStatus.CREATED,
                createToTask.createDateTime(),
                createToTask.deadlineDate(),
                createToTask.priority()
        );
        tasksMap.put(newTask.id(), newTask);
        return newTask;
    }

    public Task updateTask(Long id, Task updateToTask) {

        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id: id = " + id);
        }
        Task task = tasksMap.get(id);
        TaskStatus newStatus = task.status().equals(TaskStatus.DONE) ?
                TaskStatus.IN_PROGRESS : updateToTask.status();
        Task updateTask = new Task(
                task.id(),
                updateToTask.creatorId(),
                updateToTask.assignedUserId(),
                newStatus,
                updateToTask.createDateTime(),
                updateToTask.deadlineDate(),
                updateToTask.priority()
                );
        tasksMap.put(updateTask.id(), updateTask);
        return updateTask;
    }

    public void deleteTask(Long id) {

        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id: id = " + id);
        }
        tasksMap.remove(id);
    }
}
