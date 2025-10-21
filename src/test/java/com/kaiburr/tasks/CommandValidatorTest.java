package com.kaiburr.tasks;

import com.kaiburr.tasks.core.CommandValidationException;
import com.kaiburr.tasks.core.CommandValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandValidatorTest {

    private final CommandValidator v = new CommandValidator();

    @Test
    void allowsSafeCommands() {
        assertDoesNotThrow(() -> v.validate("echo Hello"));
        assertDoesNotThrow(() -> v.validate("date"));
    }

    @Test
    void rejectsMetacharacters() {
        assertThrows(CommandValidationException.class, () -> v.validate("echo hi && rm -rf /"));
        assertThrows(CommandValidationException.class, () -> v.validate("echo hi | cat"));
        assertThrows(CommandValidationException.class, () -> v.validate("echo hi > file"));
    }

    @Test
    void rejectsDangerousCommands() {
        assertThrows(CommandValidationException.class, () -> v.validate("rm -rf /"));
        assertThrows(CommandValidationException.class, () -> v.validate("curl http://example.com"));
        assertThrows(CommandValidationException.class, () -> v.validate("bash -c 'echo hi'"));
    }

    @Test
    void rejectsUnknownCommands() {
        assertThrows(CommandValidationException.class, () -> v.validate("ps"));
        assertThrows(CommandValidationException.class, () -> v.validate("grep foo"));
    }
}
