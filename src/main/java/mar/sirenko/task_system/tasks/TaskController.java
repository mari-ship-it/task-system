package mar.sirenko.task_system.tasks;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTask(
            @RequestParam(name = "creatorId", required = false) Long creatorId,
            @RequestParam(name = "assignedUserId", required = false) Long assignedUserId,
            @RequestParam(name = "status", required = false) TaskStatus status,
            @RequestParam(name = "priority", required = false) TaskPriority priority,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber
    ) {
        log.info("Called getAllTask");

        return ResponseEntity.status(HttpStatus.OK).
                body(taskService.searchAllByFilter(
                        new TaskSearchFilter(
                                creatorId,
                                assignedUserId,
                                status,
                                priority,
                                pageSize,
                                pageNumber
                        )));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable("id") Long id
    ) {
        log.info("Called getTaskById id = {}", id);

        return ResponseEntity.status(HttpStatus.OK).body(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task createToTask) {
        log.info("Called createTask: createToTask={}", createToTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(createToTask));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> startTask(
            @PathVariable("id") Long id
    ) {
        log.info("Celled startTask: id={}", id);
        return ResponseEntity.status(HttpStatus.OK).body(taskService.startTask(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(
            @PathVariable("id") Long id
    ) {
        log.info("Celled completeTask: id={}", id);
        return ResponseEntity.status(HttpStatus.OK).body(taskService.completeTask(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody @Valid Task updateToTask) {
        log.info("Called updateTask: id={} updateToTask={}", id, updateToTask);
        return ResponseEntity.status(HttpStatus.OK).body(taskService.updateTask(id, updateToTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {

        log.info("Called deleteTask: id={}", id);
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
