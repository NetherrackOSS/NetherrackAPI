package com.netherrack.api.permission;

/** The three states a permission node can have for a subject. */
public enum PermissionState {

    /** No rule was set; lower-priority defaults decide the answer. */
    UNDEFINED,

    /** The subject has the node. */
    ALLOW,

    /** The subject explicitly does not have the node. */
    DENY
}
