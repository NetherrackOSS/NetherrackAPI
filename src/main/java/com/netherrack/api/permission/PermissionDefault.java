package com.netherrack.api.permission;

/**
 * What an unassigned registered permission means.
 * <p>
 * Operators already have every command level up to the server's configured limit, and
 * {@link #OPERATOR} carries that idea into API permissions. {@link #UNDEFINED} leaves the node
 * unset, which is useful for permissions a plugin only uses internally.
 */
public enum PermissionDefault {

    /** Everyone has the node unless something denies it. */
    ALLOW,

    /** Nobody has the node unless something allows it. */
    DENY,

    /** Operators have the node; other players do not. */
    OPERATOR,

    /** Players who are not operators have the node. */
    NOT_OPERATOR,

    /** No default: only an explicit attachment can grant the node. */
    UNDEFINED
}
