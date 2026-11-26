package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;
import com.netherrack.api.item.ItemStack;

import java.util.Objects;

/**
 * Fired before a player drops an item. Cancelling keeps the stack in their inventory, and changing
 * the stack changes what appears in the world.
 */
public final class PlayerItemDropEvent extends PlayerEvent implements Cancellable {

    private ItemStack item;
    private boolean cancelled;

    public PlayerItemDropEvent(Player player, ItemStack item) {
        super(player);
        this.item = Objects.requireNonNull(item, "item");
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = Objects.requireNonNull(item, "item");
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
