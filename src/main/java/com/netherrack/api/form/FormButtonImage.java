package com.netherrack.api.form;

import java.util.Objects;

/**
 * An icon on a {@link SimpleForm} button, in the two forms Bedrock accepts.
 * <p>
 * This follows Cloudburst Nukkit's {@code ElementButtonImageData}: a {@code path} names a texture
 * in the client's resource pack, while a {@code url} points at an image to download. Both are
 * written into the button's {@code image} object, and an icon with empty data is treated as no
 * icon rather than sent to the client.
 */
public final class FormButtonImage {

    /** An icon from the client's resource pack, e.g. {@code textures/items/apple}. */
    public static final String TYPE_PATH = "path";

    /** An icon downloaded from a URL; the client expects an HTTPS address. */
    public static final String TYPE_URL = "url";

    private final String type;
    private final String data;

    /**
     * Creates an icon from one of the two client-supported types.
     *
     * @throws IllegalArgumentException if the type is not {@link #TYPE_PATH} or {@link #TYPE_URL}
     */
    public FormButtonImage(String type, String data) {
        if (!TYPE_PATH.equals(type) && !TYPE_URL.equals(type)) {
            throw new IllegalArgumentException("image type must be 'path' or 'url'");
        }
        this.type = type;
        this.data = Objects.requireNonNull(data, "data");
    }

    /** Uses a texture path from the client's resource pack. */
    public static FormButtonImage path(String data) {
        return new FormButtonImage(TYPE_PATH, data);
    }

    /** Uses an image downloaded from a URL. */
    public static FormButtonImage url(String data) {
        return new FormButtonImage(TYPE_URL, data);
    }

    /** Either {@link #TYPE_PATH} or {@link #TYPE_URL}. */
    public String getType() {
        return type;
    }

    /** The texture path or image URL, depending on {@link #getType()}. */
    public String getData() {
        return data;
    }

    /** Whether the client has no image to show, which makes a button text-only. */
    public boolean isEmpty() {
        return data.isEmpty();
    }
}
