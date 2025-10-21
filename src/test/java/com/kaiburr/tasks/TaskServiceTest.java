package com.kaiburr.tasks;

import com.kaiburr.tasks.core.CommandValidator;
import com.kaiburr.tasks.core.LocalCommandRunner;
import com.kaiburr.tasks.model.Task;
import com.kaiburr.tasks.repo.TaskRepository;
import com.kaiburr.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Test
    void getByIdNotFound() {
        TaskRepository repo = mock(TaskRepository.class);
        when(repo.findById("x")).thenReturn(Optional.empty());

        TaskService svc = new TaskService(repo, new CommandValidator(), new LocalCommandRunner());
        assertThrows(ResponseStatusException.class, () -> svc.getById("x"));
    }

    @Test
    void upsertValidates() {
        TaskRepository repo = mock(TaskRepository.class);
        when(repo.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        TaskService svc = new TaskService(repo, new CommandValidator(), new LocalCommandRunner());
        Task t = new Task("1", "n", "o", "echo hi");
        Task saved = svc.upsert(t);
        assertEquals("1", saved.getId());
    }
}
