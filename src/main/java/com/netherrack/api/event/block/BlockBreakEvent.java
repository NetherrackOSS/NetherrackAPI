package com.netherrack.api.event.block;

import com.netherrack.api.entity.Player;
import com.netherrack.api.world.BlockPosition;
import com.netherrack.api.world.BlockSnapshot;

/**
 * Fired before a player breaks a block, after the server's reach and game-mode checks pass.
 * Cancelling leaves the block in place and puts the client's predicted air block back.
 */
public final class BlockBreakEvent extends BlockEvent {

    public BlockBreakEvent(Player player, BlockPosition position, BlockSnapshot block) {
        super(player, position, block);
    }
}
