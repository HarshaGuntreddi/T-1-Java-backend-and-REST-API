package com.kaiburr.tasks.config;

import com.kaiburr.tasks.core.CommandValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return pd;
    }

    @ExceptionHandler(CommandValidationException.class)
    public ProblemDetail handleCommandValidation(CommandValidationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid command");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleRSE(ResponseStatusException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(ex.getStatusCode());
        pd.setTitle(ex.getReason());
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
