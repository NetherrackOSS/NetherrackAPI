package com.netherrack.api.event;

/**
 * An event whose default server action a listener may stop.
 * <p>
 * Cancelling never guarantees the client's prediction is undone by itself — the server still has
 * to do that — but it does mean the action this event guards is not carried out.
 */
public interface Cancellable {

    void setCancelled(boolean cancelled);

    boolean isCancelled();
}
