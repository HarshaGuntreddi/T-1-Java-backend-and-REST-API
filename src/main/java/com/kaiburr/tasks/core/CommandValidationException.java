package com.kaiburr.tasks.core;

public class CommandValidationException extends RuntimeException {
    public CommandValidationException(String message) {
        super(message);
    }
}
