package com.kaiburr.tasks.api;

import com.kaiburr.tasks.model.Task;
import com.kaiburr.tasks.model.TaskExecution;
import com.kaiburr.tasks.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Task byId(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/search")
    public List<Task> search(@RequestParam("q") String q) {
        return service.searchByName(q);
    }

    @PutMapping
    public Task upsert(@Valid @RequestBody Task task) {
        return service.upsert(task);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PutMapping(value = "/{id}/execute", consumes = { MediaType.TEXT_PLAIN_VALUE, MediaType.ALL_VALUE })
    public TaskExecution execute(@PathVariable String id, @RequestBody(required = false) byte[] body) {
        String override = null;
        if (body != null && body.length > 0) {
            override = new String(body, StandardCharsets.UTF_8);
        }
        return service.execute(id, override);
    }
}
