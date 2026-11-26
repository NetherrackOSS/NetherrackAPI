package com.netherrack.api.item;

import java.util.Objects;

/**
 * The small part of an item stack plugins need in events.
 * <p>
 * This is intentionally immutable. A listener that wants to change what is dropped or consumed
 * sets a new stack on the event instead of mutating the server's live item.
 */
public final class ItemStack {

    private final String id;
    private final int count;
    private final short damage;

    public ItemStack(String id, int count) {
        this(id, count, (short) 0);
    }

    public ItemStack(String id, int count, short damage) {
        this.id = Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("item id may not be blank");
        }
        if (count < 0) {
            throw new IllegalArgumentException("item count may not be negative");
        }
        this.count = count;
        this.damage = damage;
    }

    /** The namespaced item id, such as {@code minecraft:apple}. */
    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    /** The item's damage or durability value. */
    public short getDamage() {
        return damage;
    }

    /** Whether this stack represents nothing, either by count or by id. */
    public boolean isEmpty() {
        return count == 0 || id.equals("minecraft:air");
    }

    @Override
    public String toString() {
        return count + "x " + id + (damage == 0 ? "" : " damage=" + damage);
    }
}
