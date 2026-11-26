package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;
import com.netherrack.api.item.InventoryClickAction;
import com.netherrack.api.item.ItemStack;

import java.util.Objects;

/**
 * Fired before an inventory interaction is applied. Cancelling refuses the whole request, so the
 * client's predicted slot changes are undone. Delivered on the player's network thread.
 * <p>
 * One Bedrock click can contain several internal actions; the slot and stack here describe the one
 * the player acted on, not every slot the server may change while applying the request.
 */
public final class PlayerInventoryClickEvent extends PlayerEvent implements Cancellable {

    private final int slot;
    private final ItemStack item;
    private final ItemStack cursor;
    private final InventoryClickAction action;
    private boolean cancelled;

    public PlayerInventoryClickEvent(Player player, int slot, ItemStack item, ItemStack cursor,
                                     InventoryClickAction action) {
        super(player);
        this.slot = slot;
        this.item = Objects.requireNonNull(item, "item");
        this.cursor = Objects.requireNonNull(cursor, "cursor");
        this.action = Objects.requireNonNull(action, "action");
    }

    /** The slot the player acted on, or -1 when the action has no server-side slot. */
    public int getSlot() {
        return slot;
    }

    /** The stack in that slot before the interaction. */
    public ItemStack getItem() {
        return item;
    }

    /** What the player was holding on the cursor, when there is one. */
    public ItemStack getCursor() {
        return cursor;
    }

    public InventoryClickAction getAction() {
        return action;
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
