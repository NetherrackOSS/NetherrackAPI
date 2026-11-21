package com.netherrack.api.form;

/** Which button was pressed in a {@link SimpleForm}. */
public final class SimpleFormResponse {

    private final int buttonId;
    private final String buttonText;

    public SimpleFormResponse(int buttonId, String buttonText) {
        this.buttonId = buttonId;
        this.buttonText = buttonText;
    }

    /** The button's zero-based index, matching the order buttons were added. */
    public int getButtonId() {
        return buttonId;
    }

    public String getButtonText() {
        return buttonText;
    }
}
