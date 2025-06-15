package ru.tracker.exceptions;

public class ManagerAddTaskException extends RuntimeException {
    public ManagerAddTaskException(final String message) {
        super(message);
    }
}
