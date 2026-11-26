package com.netherrack.api.item;

/**
 * What a player did in one inventory interaction.
 * <p>
 * Bedrock sends a click as one item stack request that may contain several internal actions, so
 * these values name the gesture rather than every slot the server touches while applying it.
 */
public enum InventoryClickAction {

    /** Took some or all of a stack. */
    TAKE,

    /** Put a stack down onto another slot. */
    PLACE,

    /** Swapped the stack under the cursor with one in a slot. */
    SWAP,

    /** Took the result of a recipe. */
    CRAFT,

    /** Threw a stack out of the inventory. */
    DROP,

    /** Destroyed a stack in the creative inventory. */
    DESTROY,

    /** Used up an ingredient or the food that started a meal. */
    CONSUME,

    /** An interaction without a closer match. */
    OTHER
}
