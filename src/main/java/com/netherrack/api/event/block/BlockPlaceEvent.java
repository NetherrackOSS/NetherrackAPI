package com.netherrack.api.event.block;

import com.netherrack.api.entity.Player;
import com.netherrack.api.world.BlockPosition;
import com.netherrack.api.world.BlockSnapshot;

/**
 * Fired before a player places a block, after the server has checked that the placement fits.
 * Cancelling stops the placement and puts the client's predicted block back.
 */
public final class BlockPlaceEvent extends BlockEvent {

    public BlockPlaceEvent(Player player, BlockPosition position, BlockSnapshot block) {
        super(player, position, block);
    }
}
