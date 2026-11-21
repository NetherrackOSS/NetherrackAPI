package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.form.Form;

/**
 * Fired after a player answers or closes a form.
 * <p>
 * Plugins that only care about their own form should use {@link Form#onClose} or
 * {@link com.netherrack.api.form.SimpleForm#onSubmit}. This event is for listeners that want to
 * observe every form response, in the same way Nukkit's {@code PlayerFormRespondedEvent} does.
 */
public final class PlayerFormRespondedEvent extends PlayerEvent {

    private final Form form;
    private final int formId;
    private final String response;
    private final boolean closed;

    public PlayerFormRespondedEvent(Player player, Form form, int formId, String response,
                                    boolean closed) {
        super(player);
        this.form = form;
        this.formId = formId;
        this.response = response;
        this.closed = closed;
    }

    public Form getForm() {
        return form;
    }

    public int getFormId() {
        return formId;
    }

    /** The client's raw answer, such as {@code "0"}, or null when it closed the form. */
    public String getResponse() {
        return response;
    }

    public boolean wasClosed() {
        return closed;
    }
}
