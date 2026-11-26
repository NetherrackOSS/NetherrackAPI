package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;
import com.netherrack.api.item.ItemStack;
import com.netherrack.api.world.BlockPosition;

import java.util.Objects;

/**
 * Fired before a player uses what they hold, whether on a block or on air. Cancelling stops the use
 * before placement, armor equipping or eating begins. Delivered on the player's network thread.
 */
public final class PlayerItemUseEvent extends PlayerEvent implements Cancellable {

    private final ItemStack item;
    private final int slot;
    private final boolean offhand;
    private final BlockPosition blockPosition;
    private boolean cancelled;

    public PlayerItemUseEvent(Player player, ItemStack item, int slot, boolean offhand,
                              BlockPosition blockPosition) {
        super(player);
        this.item = Objects.requireNonNull(item, "item");
        this.slot = slot;
        this.offhand = offhand;
        this.blockPosition = blockPosition;
    }

    /** The stack the server has in the hand that was used. */
    public ItemStack getItem() {
        return item;
    }

    /** The hotbar slot, or -1 for the offhand. */
    public int getSlot() {
        return slot;
    }

    public boolean isOffhand() {
        return offhand;
    }

    /** The block clicked, or null when the item was used on air. */
    public BlockPosition getBlockPosition() {
        return blockPosition;
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
