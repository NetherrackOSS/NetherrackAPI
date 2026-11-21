package com.netherrack.api.form;

import com.netherrack.api.entity.Player;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base for the JSON forms Bedrock lets a server show to a player.
 * <p>
 * Nukkit keeps the open form on the player, sends a packet describing it, then feeds the client's
 * answer back into that same object. This API follows that shape so the server only has to manage
 * packet ids: a form knows how to serialize itself and what to do with a response. One player can
 * only have one form open at a time, as in the vanilla client.
 */
public abstract class Form {

    private final String type;
    private String title = "";
    private Consumer<Player> closeHandler;

    protected Form(String type, String title) {
        this.type = Objects.requireNonNull(type, "type");
        this.title = title == null ? "" : title;
    }

    /** The value Bedrock expects in the JSON's {@code type} field. */
    public final String getType() {
        return type;
    }

    public final String getTitle() {
        return title;
    }

    public final Form setTitle(String title) {
        this.title = title == null ? "" : title;
        return this;
    }

    /** Registers a callback for the player dismissing the form without choosing anything. */
    public Form onClose(Consumer<Player> closeHandler) {
        this.closeHandler = Objects.requireNonNull(closeHandler, "closeHandler");
        return this;
    }

    /** The JSON sent to the client. Implementations must escape all player-provided text. */
    public abstract String toJson();

    protected final Consumer<Player> closeHandler() {
        return closeHandler;
    }

    /**
     * Delivered by the server when the client answers. A null, blank or literal {@code null}
     * response means the form was closed, which is also how older clients report cancellation.
     */
    public final void handleResponse(Player player, String data, boolean closed) {
        Objects.requireNonNull(player, "player");
        if (closed || data == null || data.isBlank() || data.trim().equals("null")) {
            if (closeHandler != null) {
                closeHandler.accept(player);
            }
            return;
        }
        handleSubmit(player, data.trim());
    }

    protected abstract void handleSubmit(Player player, String data);

    /** JSON string literal for one field, escaping control characters as well as quotes. */
    static String quote(String value) {
        StringBuilder out = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append("\\u");
                        String hex = Integer.toHexString(c);
                        for (int pad = hex.length(); pad < 4; pad++) {
                            out.append('0');
                        }
                        out.append(hex);
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.append('"').toString();
    }
}
