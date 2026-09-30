package mar.sirenko.task_system.tasks;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository taskRepository, TaskMapper mapper) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Task getTaskById(
            Long id
    ) {
        return taskRepository.findById(id)
                .map(mapper::toTask)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<Task> searchAllByFilter(
            TaskSearchFilter filter
    ) {
        Pageable pageable = PageRequest.of(filter.pageNumber(), filter.pageSize());

        return taskRepository.searchAllByFilter(
                filter.creatorId(),
                filter.assignedUserId(),
                filter.status(),
                filter.priority(),
                pageable
                )
                .stream()
                .map(mapper::toTask)
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
            return mapper.toTask(taskEntity);
        }
        validateActiveTasksLimit(taskEntity.getAssignedUserId());


        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        return mapper.toTask(taskEntity);
    }

    @Transactional
    public Task createTask(
            Task createToTask
    ) {
        if (createToTask.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        if (createToTask.deadlineDate() == null || !createToTask.deadlineDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Must contain a future date deadline");
        }

        TaskEntity entityToSave = mapper.toEntity(createToTask);
        entityToSave.setStatus(TaskStatus.CREATED);
        entityToSave.setCreateDateTime(LocalDateTime.now());

        TaskEntity savedEntity = taskRepository.save(entityToSave);
        return mapper.toTask(savedEntity);
    }

    @Transactional
    public Task updateTask(
            Long id, Task taskToUpdate
    ) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id));

        if (taskEntity.getStatus() == TaskStatus.DONE && taskToUpdate.status() != TaskStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Updated not allowed");
        }

        TaskStatus newStatus = taskToUpdate.status() != null ? taskToUpdate.status() : taskEntity.getStatus();
        Long targetUserId = taskToUpdate.assignedUserId() != null
                ? taskToUpdate.assignedUserId()
                : taskEntity.getAssignedUserId();

        if (newStatus == TaskStatus.IN_PROGRESS && taskEntity.getStatus() != TaskStatus.IN_PROGRESS) {

            if (targetUserId == null) {
                throw new IllegalArgumentException("AssignedUserId should not be empty when starting a task");
            }
            validateActiveTasksLimit(targetUserId);
        }

        taskEntity.setStatus(newStatus);
        taskEntity.setAssignedUserId(targetUserId);

        if (taskToUpdate.priority() != null) {
            taskEntity.setPriority(taskToUpdate.priority());
        }
        if (taskToUpdate.deadlineDate() != null) {
            taskEntity.setDeadlineDate(taskToUpdate.deadlineDate());
        }
        return mapper.toTask(taskEntity);
    }

    @Transactional
    public Task completeTask(
            Long id
    ) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id));

        if (taskEntity.getAssignedUserId() == null) {
            throw new IllegalArgumentException("AssignedUserId should be not empty");
        }

        if (taskEntity.getDeadlineDate() == null) {
            throw new IllegalArgumentException("DeadlineDate should be not empty");
        }

        if (taskEntity.getStatus() != TaskStatus.DONE) {
            taskEntity.setStatus(TaskStatus.DONE);
            taskEntity.setDoneDateTime(LocalDateTime.now());
            log.info("Task was successfully complete id={}", id);
        }
        return mapper.toTask(taskEntity);
    }

    @Transactional
    public void deleteTask(
            Long id
    ) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id: id = " + id));

        taskRepository.delete(taskEntity);
        log.info("Successfully deleted task with id={}", id);
    }

    private void validateActiveTasksLimit(Long assignedUserId) {
        if (assignedUserId == null) {
            return;
        }

        int activeTaskCount = taskRepository.countActiveTasksWithLimit(assignedUserId);

        if (activeTaskCount >= 5) {
            throw new IllegalArgumentException(
                    "The user has already reached the maximum limit of 5 active tasks"
            );
        }
    }

}
