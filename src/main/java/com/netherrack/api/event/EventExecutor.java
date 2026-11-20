package com.netherrack.api.event;

/** Invokes one handler method. RegisteredHandler builds this from the reflected method. */
@FunctionalInterface
public interface EventExecutor {

    void execute(Listener listener, Event event) throws Throwable;
}
