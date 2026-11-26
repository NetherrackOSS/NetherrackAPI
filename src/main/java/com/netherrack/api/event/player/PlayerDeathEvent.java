package com.netherrack.api.event.player;

import com.netherrack.api.entity.DamageCause;
import com.netherrack.api.entity.Player;
import com.netherrack.api.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fired after a player dies but before their drops are spawned and the death message is sent.
 * Listeners may edit the message or the drop list; the event cannot be cancelled because death has
 * already happened.
 */
public final class PlayerDeathEvent extends PlayerEvent {

    private final DamageCause cause;
    private final List<ItemStack> drops = new ArrayList<>();
    private String deathMessage;

    public PlayerDeathEvent(Player player, DamageCause cause, String deathMessage,
                            List<ItemStack> drops) {
        super(player);
        this.cause = Objects.requireNonNull(cause, "cause");
        setDeathMessage(deathMessage);
        if (drops != null) {
            this.drops.addAll(drops);
        }
    }

    public DamageCause getCause() {
        return cause;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage == null ? "" : deathMessage;
    }

    /** The live list of stacks the server will drop; adding or removing here changes the drops. */
    public List<ItemStack> getDrops() {
        return drops;
    }
}
