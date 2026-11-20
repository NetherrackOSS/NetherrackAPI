package com.netherrack.api.event;

import com.netherrack.api.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Registration and dispatch for events, kept independent of any server implementation.
 * <p>
 * The server owns one of these and exposes it through PluginManager. Handler methods are found
 * once at registration time rather than on every event, which keeps dispatch cheap and lets a
 * bad method be rejected when the plugin is enabled instead of much later during play. A listener
 * for a base class such as PlayerEvent also receives its subclasses; handlers from the whole
 * hierarchy are ordered together by priority.
 */
public final class EventManager {

    private final Map<Class<? extends Event>, HandlerList> handlers = new ConcurrentHashMap<>();
    private final Consumer<EventException> onError;

    public EventManager() {
        this(error -> { });
    }

    public EventManager(Consumer<EventException> onError) {
        this.onError = Objects.requireNonNull(onError, "onError");
    }

    /** Registers every valid @EventHandler method on the listener for the given plugin. */
    public void registerEvents(Listener listener, Plugin plugin) {
        Objects.requireNonNull(listener, "listener");
        Objects.requireNonNull(plugin, "plugin");
        List<RegisteredHandler> found = new ArrayList<>();
        for (Method method : listener.getClass().getMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null) {
                continue;
            }
            if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                throw new EventException("Handler " + method + " must take exactly one Event parameter");
            }
            if (method.getReturnType() != void.class) {
                throw new EventException("Handler " + method + " must return void");
            }
            if (Modifier.isStatic(method.getModifiers())) {
                throw new EventException("Handler " + method + " must not be static");
            }
            // Listeners are often private inner classes; make the public handler method callable
            // from this package instead of leaving that to each plugin author.
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
            found.add(new RegisteredHandler(plugin, listener, eventClass,
                    annotation.priority(), annotation.ignoreCancelled(),
                    (target, event) -> method.invoke(target, event)));
        }
        // Register only after every method validated, so a bad handler can't leave half a
        // listener active.
        for (RegisteredHandler handler : found) {
            handlers.computeIfAbsent(handler.getEventClass(), key -> new HandlerList()).register(handler);
        }
    }

    /** Removes every handler belonging to one listener object. */
    public void unregisterEvents(Listener listener) {
        for (HandlerList list : handlers.values()) {
            list.unregister(listener);
        }
    }

    /** Removes every handler registered by a plugin, called when that plugin is disabled. */
    public void unregisterEvents(Plugin plugin) {
        for (HandlerList list : handlers.values()) {
            list.unregister(plugin);
        }
    }

    /**
     * Delivers an event to its handlers, in priority order across its class hierarchy. A handler's
     * failure is reported and dispatch goes on, so one broken plugin can't silence all the others.
     */
    public void callEvent(Event event) {
        List<RegisteredHandler> applicable = new ArrayList<>();
        for (Class<?> type = event.getClass(); Event.class.isAssignableFrom(type); type = type.getSuperclass()) {
            @SuppressWarnings("unchecked")
            HandlerList list = handlers.get((Class<? extends Event>) type);
            if (list != null) {
                applicable.addAll(list.getRegisteredHandlers());
            }
        }
        applicable.sort(Comparator.comparingInt(handler -> handler.getPriority().ordinal()));
        for (RegisteredHandler handler : applicable) {
            try {
                handler.invoke(event);
            } catch (EventException e) {
                onError.accept(e);
            }
        }
    }
}
