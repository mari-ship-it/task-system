package mar.sirenko.task_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTask() {

        log.info("Called getAllTask");
        return ResponseEntity.status(HttpStatus.OK).body(taskService.findAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {

        log.info("Called getTaskById id = {}", id);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(taskService.getTaskById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task createToTask) {

        log.info("Called createTask: createToTask={}", createToTask);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(createToTask));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody Task updateToTask) {

        log.info("Called updateTask: id={} updateToTask={}", id, updateToTask);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(taskService.updateTask(id, updateToTask));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {

        log.info("Called deleteTask: id={}", id);
        try {
            taskService.deleteTask(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}