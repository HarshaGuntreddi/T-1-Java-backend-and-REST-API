package com.kaiburr.tasks.service;

import com.kaiburr.tasks.core.CommandValidationException;
import com.kaiburr.tasks.core.CommandValidator;
import com.kaiburr.tasks.core.LocalCommandRunner;
import com.kaiburr.tasks.model.Task;
import com.kaiburr.tasks.model.TaskExecution;
import com.kaiburr.tasks.repo.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repo;
    private final CommandValidator validator;
    private final LocalCommandRunner runner;

    public TaskService(TaskRepository repo, CommandValidator validator, LocalCommandRunner runner) {
        this.repo = repo;
        this.validator = validator;
        this.runner = runner;
    }

    public List<Task> findAll() {
        return repo.findAll();
    }

    public Task getById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "task not found"));
    }

    public List<Task> searchByName(String q) {
        List<Task> list = repo.findByNameContainingIgnoreCase(q);
        if (list == null || list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no tasks match: " + q);
        }
        return list;
    }

    public Task upsert(Task task) {
        validator.validate(task.getCommand());
        // Save (insert or update). If you prefer immutable ids, validate here.
        return repo.save(task);
    }

    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "task not found");
        repo.deleteById(id);
    }

    public TaskExecution execute(String id, String overrideCommand) {
        Task task = getById(id);
        String toRun = (overrideCommand != null && !overrideCommand.isBlank()) ? overrideCommand : task.getCommand();
        validator.validate(toRun);

        try {
            LocalCommandRunner.Result r = runner.run(toRun);
            String output = r.output + (r.exitCode == 0 ? "" : ("(exit=" + r.exitCode + ")"));
            TaskExecution te = new TaskExecution(r.startTime, r.endTime, output);
            task.getTaskExecutions().add(te);
            repo.save(task);
            return te;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "failed to run command: " + e.getMessage(), e);
        }
    }
}
