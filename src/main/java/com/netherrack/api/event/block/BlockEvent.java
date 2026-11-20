package com.netherrack.api.event.block;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;
import com.netherrack.api.event.Event;
import com.netherrack.api.world.BlockPosition;
import com.netherrack.api.world.BlockSnapshot;

/**
 * Base for an event about one block and the player changing it.
 * <p>
 * These events are delivered on the player's network thread, where Netherrack currently handles
 * block actions. Handlers must return quickly; use the server scheduler for slower work.
 */
public abstract class BlockEvent extends Event implements Cancellable {

    private final Player player;
    private final BlockPosition position;
    private final BlockSnapshot block;
    private boolean cancelled;

    protected BlockEvent(Player player, BlockPosition position, BlockSnapshot block) {
        this.player = player;
        this.position = position;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    /** The block's coordinates in the world. */
    public BlockPosition getPosition() {
        return position;
    }

    /** An immutable copy of the block being placed or broken. */
    public BlockSnapshot getBlock() {
        return block;
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
