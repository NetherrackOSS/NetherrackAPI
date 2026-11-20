package com.netherrack.api.world;

import java.util.Objects;

/**
 * A point in a world, with the direction a player is facing.
 * <p>
 * The y coordinate is where the player's feet are, not their eyes: it can be passed straight to
 * {@link World#getBlock(int, int, int)} and matches the y a vanilla {@code /tp} shows. Bedrock
 * movement packets carry eye height instead, so the server adapter adds it when teleporting and
 * removes it when reading a position. yaw and pitch are in degrees, as Bedrock sends them.
 */
public final class Location {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public Location(double x, double y, double z) {
        this(x, y, z, 0f, 0f);
    }

    public Location(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    /** The block containing this point, for block lookups and placement. */
    public BlockPosition toBlockPosition() {
        return new BlockPosition((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Location location)) {
            return false;
        }
        return Double.compare(x, location.x) == 0 && Double.compare(y, location.y) == 0
                && Double.compare(z, location.z) == 0 && Float.compare(yaw, location.yaw) == 0
                && Float.compare(pitch, location.pitch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ") yaw=" + yaw + " pitch=" + pitch;
    }
}
