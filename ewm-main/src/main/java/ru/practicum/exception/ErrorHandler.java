package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            CategoryNotFoundException.class,
            EventNotFoundException.class,
            RequestNotFoundException.class,
            NotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(RuntimeException e) {
        log.warn("Not found: {}", e.getMessage());
        return new ApiError(
                Collections.emptyList(),
                e.getMessage(),
                "The required object was not found.",
                "NOT_FOUND",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({
            DataConflictException.class,
            DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(Exception e) {
        log.warn("Conflict: {}", e.getMessage());
        return new ApiError(
                Collections.emptyList(),
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                "CONFLICT",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({
            BadRequestException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException) {
            message = ((MethodArgumentNotValidException) e).getBindingResult()
                    .getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else {
            message = e.getMessage();
        }

        log.warn("Bad request: {}", message);
        return new ApiError(
                Collections.emptyList(),
                message,
                "Incorrectly made request.",
                "BAD_REQUEST",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        return new ApiError(
                Collections.emptyList(),
                "Internal server error",
                "Unexpected error occurred.",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );
    }
}