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
 * {@code FormWindowSimple}: each button is an object with its own {@code type}, {@code text} and
 * optional {@code image}, so labels can contain anything without changing the surrounding
 * structure.
 */
public final class SimpleForm extends Form {

    private final String content;
    private final List<Button> buttons = new ArrayList<>();
    private BiConsumer<Player, SimpleFormResponse> submitHandler;

    private record Button(String text, FormButtonImage image) {
    }

    public SimpleForm(String title, String content) {
        super("form", title);
        this.content = content == null ? "" : content;
    }

    /** Adds a button; its position is the id delivered in {@link SimpleFormResponse}. */
    public SimpleForm addButton(String text) {
        return addButton(text, null);
    }

    /**
     * Adds a button with an optional icon, using the same id ordering as
     * {@link #addButton(String)}. An icon whose data is empty is ignored, as in Nukkit's
     * {@code ElementButton}.
     */
    public SimpleForm addButton(String text, FormButtonImage image) {
        buttons.add(new Button(text == null ? "" : text,
                image == null || image.isEmpty() ? null : image));
        return this;
    }

    /** The button labels, in the order their ids are assigned. */
    public List<String> getButtons() {
        return buttons.stream().map(Button::text).toList();
    }

    /** The icon on the button with this id, or null for a text-only button or an unknown id. */
    public FormButtonImage getButtonImage(int id) {
        if (id < 0 || id >= buttons.size()) {
            return null;
        }
        return buttons.get(id).image();
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
            Button button = buttons.get(i);
            out.append("{\"type\":\"button\",\"text\":").append(quote(button.text()));
            FormButtonImage image = button.image();
            if (image != null) {
                out.append(",\"image\":{\"type\":").append(quote(image.getType()))
                        .append(",\"data\":").append(quote(image.getData())).append('}');
            }
            out.append('}');
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
        submitHandler.accept(player,
                new SimpleFormResponse(buttonId, buttons.get(buttonId).text()));
    }
}
