package com.netherrack.api.entity;

import com.netherrack.api.form.Form;
import com.netherrack.api.permission.Permissible;
import com.netherrack.api.world.Location;
import com.netherrack.api.world.World;

import java.util.UUID;

/**
 * A connected player as plugins see them.
 * <p>
 * This is deliberately narrow: enough for the first events to identify who acted and answer
 * them. More of the player will be added as events need it.
 */
public interface Player extends Permissible {

    String getName();

    UUID getUuid();

    /** The Xbox User ID, or an empty string for offline/self-signed logins. */
    String getXuid();

    World getWorld();

    /**
     * Where the player is, at feet height and with the yaw and pitch their client reports.
     * The returned value is a copy; changing it does not move the player.
     */
    Location getLocation();

    /** Moves the player, their chunk view and everyone else's view of them in one step. */
    void teleport(Location location);

    GameMode getGameMode();

    void setGameMode(GameMode gameMode);

    void sendMessage(String message);

    /**
     * Shows a message in the player's action bar. Fires a cancellable PlayerActionBarEvent with
     * Nukkit's default animation times of one tick in, no stay and one tick out.
     */
    void sendActionBar(String message);

    /** Shows an action bar message with explicit fade-in, stay and fade-out times in ticks. */
    void sendActionBar(String message, int fadeInTime, int stayTime, int fadeOutTime);

    /** Shows a toast notification. Fires a cancellable PlayerToastEvent before sending it. */
    void sendToast(String title, String content);

    /** Shows a popup message. Fires a cancellable PlayerPopupEvent before sending it. */
    void sendPopup(String message);

    /**
     * Shows a Bedrock form and returns its id, or -1 if this player already has a form open.
     * Callbacks registered on the form run on the player's network thread when the client answers.
     */
    int showForm(Form form);

    void kick(String reason);
}
