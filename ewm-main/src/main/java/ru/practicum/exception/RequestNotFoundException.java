package ru.practicum.exception;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
