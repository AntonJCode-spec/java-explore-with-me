package ru.practicum.exception;

public class CategoryConflictException extends DataConflictException {
    public CategoryConflictException(String message) {
        super(message);
    }
}
