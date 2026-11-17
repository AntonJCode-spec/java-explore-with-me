package ru.practicum.exception;

public class UserEmailConflictException extends DataConflictException {
    public UserEmailConflictException(String message) {
        super(message);
    }
}
