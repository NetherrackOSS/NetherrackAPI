package com.netherrack.api.event;

import com.netherrack.api.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

/**
 * One annotated listener method, bound to the plugin that registered it.
 * <p>
 * Keeping the plugin is what makes unregister-on-disable possible: when a plugin is disabled the
 * manager drops every handler that belongs to it, so no stale listener can run after its
 * classloader has been closed.
 */
public final class RegisteredHandler {

    private final Plugin plugin;
    private final Listener listener;
    private final Class<? extends Event> eventClass;
    private final EventPriority priority;
    private final boolean ignoreCancelled;
    private final EventExecutor executor;

    public RegisteredHandler(Plugin plugin, Listener listener, Class<? extends Event> eventClass,
                             EventPriority priority, boolean ignoreCancelled, EventExecutor executor) {
        this.plugin = plugin;
        this.listener = listener;
        this.eventClass = eventClass;
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.executor = executor;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Listener getListener() {
        return listener;
    }

    public Class<? extends Event> getEventClass() {
        return eventClass;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public boolean ignoresCancelled() {
        return ignoreCancelled;
    }

    /** Calls the listener unless cancellation should keep it from seeing this event. */
    public void invoke(Event event) {
        if (ignoreCancelled && event instanceof Cancellable cancellable && cancellable.isCancelled()) {
            return;
        }
        try {
            executor.execute(listener, event);
        } catch (InvocationTargetException e) {
            throw new EventException(e.getCause() == null ? e : e.getCause());
        } catch (Throwable t) {
            throw new EventException(t);
        }
    }
}
