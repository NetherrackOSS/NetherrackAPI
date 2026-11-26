package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;
import com.netherrack.api.item.ItemStack;

import java.util.Objects;

/**
 * Fired before a player starts eating an item. Cancelling leaves the stack and hunger unchanged;
 * changing the item changes what the server records as the food being eaten.
 */
public final class PlayerItemConsumeEvent extends PlayerEvent implements Cancellable {

    private ItemStack item;
    private boolean cancelled;

    public PlayerItemConsumeEvent(Player player, ItemStack item) {
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
