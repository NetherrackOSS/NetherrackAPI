package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;
import com.netherrack.api.item.ItemStack;

import java.util.Objects;

/** Fired before a dropped item stack is added to a player's inventory. Cancelling leaves it on the ground. */
public final class PlayerItemPickupEvent extends PlayerEvent implements Cancellable {

    private final ItemStack item;
    private boolean cancelled;

    public PlayerItemPickupEvent(Player player, ItemStack item) {
        super(player);
        this.item = Objects.requireNonNull(item, "item");
    }

    public ItemStack getItem() {
        return item;
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
