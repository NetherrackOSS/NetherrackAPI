package com.netherrack.api.form;

import com.netherrack.api.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A Bedrock simple form: a title, some message text and a list of buttons.
 * <p>
 * This is the form type the client shows for a single question or menu, and the one used by the
 * example plugin's test command. The JSON layout follows Cloudburst Nukkit's
 * {@code FormWindowSimple}: each button is an object with its own {@code type} and {@code text}, so
 * button labels can contain anything without changing the surrounding structure.
 */
public final class SimpleForm extends Form {

    private final String content;
    private final List<String> buttons = new ArrayList<>();
    private BiConsumer<Player, SimpleFormResponse> submitHandler;

    public SimpleForm(String title, String content) {
        super("form", title);
        this.content = content == null ? "" : content;
    }

    /** Adds a button; its position is the id delivered in {@link SimpleFormResponse}. */
    public SimpleForm addButton(String text) {
        buttons.add(text == null ? "" : text);
        return this;
    }

    public List<String> getButtons() {
        return List.copyOf(buttons);
    }

    public SimpleForm onSubmit(BiConsumer<Player, SimpleFormResponse> submitHandler) {
        this.submitHandler = Objects.requireNonNull(submitHandler, "submitHandler");
        return this;
    }

    @Override
    public SimpleForm onClose(Consumer<Player> closeHandler) {
        super.onClose(closeHandler);
        return this;
    }

    @Override
    public String toJson() {
        StringBuilder out = new StringBuilder("{\"type\":").append(quote(getType()))
                .append(",\"title\":").append(quote(getTitle()))
                .append(",\"content\":").append(quote(content))
                .append(",\"buttons\":[");
        for (int i = 0; i < buttons.size(); i++) {
            if (i > 0) {
                out.append(',');
            }
            out.append("{\"type\":\"button\",\"text\":").append(quote(buttons.get(i))).append('}');
        }
        return out.append("]}").toString();
    }

    @Override
    protected void handleSubmit(Player player, String data) {
        int buttonId;
        try {
            buttonId = Integer.parseInt(data);
        } catch (NumberFormatException e) {
            // A response that isn't a button index is no better than a dismissal.
            if (closeHandler() != null) {
                closeHandler().accept(player);
            }
            return;
        }
        if (buttonId < 0 || buttonId >= buttons.size() || submitHandler == null) {
            return;
        }
        submitHandler.accept(player, new SimpleFormResponse(buttonId, buttons.get(buttonId)));
    }
}
