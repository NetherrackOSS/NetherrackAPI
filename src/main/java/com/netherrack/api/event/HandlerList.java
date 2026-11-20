package com.netherrack.api.event;

import com.netherrack.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The handlers registered for one event class.
 * <p>
 * The list is sorted only when dispatch needs it, so many registrations at startup don't pay for
 * a sort each time. Handlers of equal priority keep registration order, which is the stable,
 * predictable behavior plugins expect.
 */
public final class HandlerList {

    private final List<RegisteredHandler> handlers = new ArrayList<>();
    private boolean sorted = true;

    public synchronized void register(RegisteredHandler handler) {
        handlers.add(handler);
        sorted = false;
    }

    public synchronized void unregister(RegisteredHandler handler) {
        handlers.remove(handler);
    }

    public synchronized void unregister(Listener listener) {
        handlers.removeIf(handler -> handler.getListener() == listener);
    }

    public synchronized void unregister(Plugin plugin) {
        handlers.removeIf(handler -> handler.getPlugin() == plugin);
    }

    /** Handlers in dispatch order. Callers must not modify the returned list. */
    public synchronized List<RegisteredHandler> getRegisteredHandlers() {
        if (!sorted) {
            handlers.sort(Comparator.comparingInt(handler -> handler.getPriority().ordinal()));
            sorted = true;
        }
        return List.copyOf(handlers);
    }
}
