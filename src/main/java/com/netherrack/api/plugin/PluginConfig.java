package com.netherrack.api.plugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A plugin's config.yml.
 * <p>
 * Just enough YAML for settings: nested "key: value" lines become dotted keys, so a section
 * called database with a host key is read as "database.host". Lists and block scalars aren't
 * supported yet. Values are kept as strings and converted on the way out, which keeps writing
 * a config predictable - a plugin sets a value and gets the same value back.
 */
public final class PluginConfig {

    private final Path file;
    private final Map<String, String> values = new LinkedHashMap<>();

    public PluginConfig(Path file) {
        this.file = file;
    }

    /** The file this config is stored in. */
    public Path getFile() {
        return file;
    }

    /** Reads config.yml if it exists. A missing file is normal and leaves an empty config. */
    public void load() {
        if (!Files.exists(file)) {
            return;
        }
        values.clear();
        Deque<String> sections = new ArrayDeque<>();
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String withoutComment = withoutComment(line);
                if (withoutComment.isBlank()) {
                    continue;
                }
                int indent = withoutComment.length() - withoutComment.stripLeading().length();
                while (sections.size() > indent / 2) {
                    sections.removeLast();
                }
                String text = withoutComment.strip();
                int colon = text.indexOf(':');
                if (colon < 0) {
                    continue;
                }
                String key = text.substring(0, colon).strip();
                String value = unquote(text.substring(colon + 1).strip());
                if (value.isEmpty()) {
                    sections.addLast(key);
                } else {
                    values.put(prefix(sections) + key, value);
                }
            }
        } catch (IOException e) {
            values.clear();
        }
    }

    /** Every key currently in the config. */
    public Set<String> keys() {
        return Set.copyOf(values.keySet());
    }

    public String get(String key, String fallback) {
        return values.getOrDefault(key, fallback);
    }

    public int getInt(String key, int fallback) {
        try {
            return Integer.parseInt(get(key, String.valueOf(fallback)).strip());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public boolean getBoolean(String key, boolean fallback) {
        String value = get(key, String.valueOf(fallback)).strip();
        return value.equalsIgnoreCase("true") ? true : value.equalsIgnoreCase("false") ? false : fallback;
    }

    public void set(String key, Object value) {
        values.put(key, String.valueOf(value));
    }

    /** Writes the config, creating its parent directory if needed. */
    public void save() throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            text.append(entry.getKey()).append(": ").append(quote(entry.getValue())).append('\n');
        }
        Files.writeString(file, text, StandardCharsets.UTF_8);
    }

    private static String prefix(Deque<String> sections) {
        if (sections.isEmpty()) {
            return "";
        }
        return String.join(".", sections) + ".";
    }

    private static String withoutComment(String line) {
        char quote = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quote != 0) {
                if (c == quote) {
                    quote = 0;
                }
            } else if (c == '"' || c == '\'') {
                quote = c;
            } else if (c == '#' && (i == 0 || Character.isWhitespace(line.charAt(i - 1)))) {
                return line.substring(0, i);
            }
        }
        return line;
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\\\"", "\"");
        }
        return value;
    }

    private static String quote(String value) {
        if (value.isEmpty() || value.startsWith(" ") || value.endsWith(" ")
                || value.contains(": ") || value.contains("#") || value.contains("\n")) {
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
        }
        return value;
    }
}
