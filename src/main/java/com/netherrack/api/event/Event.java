package com.netherrack.api.event;

/**
 * Base class for everything the server announces to plugins.
 * <p>
 * Events are plain value objects: a listener may read them, and may modify them only when the
 * event itself offers setters (a cancellable event's cancelled flag, a chat event's message, and
 * so on). The server calls handlers synchronously on the thread doing the action, so a handler
 * should return quickly and leave slow work to a scheduler task in a later phase.
 */
public abstract class Event {

    private final long timestamp = System.currentTimeMillis();

    /** The simple class name, such as "PlayerJoinEvent". */
    public String getEventName() {
        return getClass().getSimpleName();
    }

    /** When the event object was created, as milliseconds since the Unix epoch. */
    public long getTimestamp() {
        return timestamp;
    }
}
