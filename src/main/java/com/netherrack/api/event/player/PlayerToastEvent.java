package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

/**
 * Fired just before a toast notification is sent to one player.
 * <p>
 * Handlers may rewrite the title or content, or cancel the send entirely. Delivered on the
 * receiving player's network thread.
 */
public final class PlayerToastEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private String title;
    private String content;

    public PlayerToastEvent(Player player, String title, String content) {
        super(player);
        this.title = title == null ? "" : title;
        this.content = content == null ? "" : content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
