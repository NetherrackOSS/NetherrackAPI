package com.netherrack.api.event;

/** Wraps a failure thrown by a listener so dispatch can report it without stopping the event. */
public class EventException extends RuntimeException {

    public EventException(String message) {
        super(message);
    }

    public EventException(Throwable cause) {
        super(cause);
    }

    public EventException(String message, Throwable cause) {
        super(message, cause);
    }
}
