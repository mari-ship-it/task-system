package mar.sirenko.task_system;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public Task getTaskById(
            Long id
    ) {
        return taskRepository.findById(id)
                .map(this::toDomainTask)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<Task> findAllTasks(
    ) {
        return taskRepository.findAll()      //возможна выгрузка большого к-ва данных(добавить пагинацию)
                .stream()
                .map(this::toDomainTask)
                .toList();
    }

    @Transactional
    public Task startTask(
            Long id
    ) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id)
                );

        Long assignedUserId = taskEntity.getAssignedUserId();

        if (assignedUserId == null) {
            throw new IllegalArgumentException("AssignedUserId should be not empty");
        }

        if (taskEntity.getStatus() == TaskStatus.IN_PROGRESS) {
            return toDomainTask(taskEntity);
        }

        Long activeTaskCount = taskRepository.countByAssignedUserIdAndStatus(
                assignedUserId, TaskStatus.IN_PROGRESS);

        if (activeTaskCount > 4) {
            throw new IllegalArgumentException("More than 4 active tasks");
        }

        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        return toDomainTask(taskEntity);
    }

    @Transactional
    public Task createTask(
            Task createToTask
    ) {
        if (createToTask.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (createToTask.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        TaskEntity entityToSave = new TaskEntity(
                null,
                createToTask.creatorId(),
                createToTask.assignedUserId(),
                TaskStatus.CREATED,
                LocalDateTime.now(),
                createToTask.deadlineDate(),
                createToTask.priority()
        );
        TaskEntity savedEntity = taskRepository.save(entityToSave);
        return toDomainTask(savedEntity);
    }

    @Transactional
    public Task updateTask(
            Long id, Task taskToUpdate
    ) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id));

        TaskStatus newStatus = taskEntity.getStatus() == TaskStatus.DONE ?
                TaskStatus.IN_PROGRESS : taskEntity.getStatus();

        if (newStatus == TaskStatus.IN_PROGRESS && taskEntity.getStatus() == TaskStatus.DONE) {
            Long assignedUserId = taskToUpdate.assignedUserId();

            if (assignedUserId == null) {
                throw new IllegalArgumentException("AssignedUserId should be not empty");
            }

            Long activeTaskCount = taskRepository.countByAssignedUserIdAndStatus(
                    assignedUserId, TaskStatus.IN_PROGRESS);

            if (activeTaskCount > 4) {
                throw new IllegalArgumentException("More than 4 active tasks");
            }
        }

        taskEntity.setCreatorId(taskToUpdate.creatorId());
        taskEntity.setAssignedUserId(taskToUpdate.assignedUserId());
        taskEntity.setStatus(newStatus);
        taskEntity.setDeadlineDate(taskToUpdate.deadlineDate());
        taskEntity.setPriority(taskToUpdate.priority());

        return toDomainTask(taskEntity);
    }

    @Transactional
    public void deleteTask(
            Long id
    ) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id));

        taskRepository.delete(taskEntity);
    }

    private Task toDomainTask(
            TaskEntity task
    ) {
        return new Task(
                task.getId(),
                task.getCreatorId(),
                task.getAssignedUserId(),
                task.getStatus(),
                task.getCreateDateTime(),
                task.getDeadlineDate(),
                task.getPriority()
        );
    }

}
