package ru.practicum.exception;

public class IllegalRangeException extends BadRequestException {
    public IllegalRangeException(String message) {
        super(message);
    }
}
