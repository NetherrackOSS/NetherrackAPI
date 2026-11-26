package com.netherrack.api.entity;

/**
 * Why a player lost health.
 * <p>
 * Causes follow the sources Netherrack can actually produce rather than every vanilla damage
 * type. Plugin code should treat {@link #CUSTOM} as the escape hatch until a more specific cause
 * is added.
 */
public enum DamageCause {

    /** Another living entity, normally a melee attack. */
    ATTACK,

    /** An arrow or other projectile. */
    PROJECTILE,

    /** An explosion. */
    EXPLOSION,

    /** Landing after a fall. */
    FALL,

    /** Burning, including fire tick damage after leaving a hazard. */
    FIRE,

    /** Standing in lava. */
    LAVA,

    /** Running out of air underwater. */
    DROWNING,

    /** Being inside a solid block. */
    SUFFOCATION,

    /** Starving with no food. */
    STARVATION,

    /** Falling out of the world. */
    VOID,

    /** Potions, magic and other unexplained sources. */
    MAGIC,

    /** Contact with a harmful block such as a cactus. */
    BLOCK,

    /** Contact with an entity whose presence is harmful. */
    ENTITY,

    /** A cause supplied by a plugin or a source without a closer match. */
    CUSTOM
}
