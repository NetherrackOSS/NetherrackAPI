package com.netherrack.api.plugin;

import com.netherrack.api.ApiVersion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The contents of a plugin's plugin.yml.
 * <p>
 * Only the YAML this needs is understood: top-level "key: value" lines, lists written either
 * inline as "[a, b]" or as indented "- a" lines, quoted values, and # comments. Keeping the
 * reader here rather than adding a YAML dependency means plugin jars stay small and the API
 * keeps its promise of having no dependencies. Unknown keys are ignored so a plugin can carry
 * settings for a newer server without failing on an older one.
 */
public final class PluginDescription {

    private final String name;
    private final String version;
    private final String apiVersion;
    private final String main;
    private final String description;
    private final String author;
    private final PluginLoadOrder load;
    private final List<String> depend;
    private final List<String> softDepend;
    private final List<String> loadBefore;

    private PluginDescription(Map<String, String> values, Map<String, List<String>> lists) {
        this.name = required(values, "name");
        if (!name.matches("[A-Za-z0-9._-]+")) {
            throw new PluginDescriptionException("plugin.yml name \"" + name
                    + "\" may only contain letters, digits, dots, dashes and underscores");
        }
        this.version = required(values, "version");
        this.main = required(values, "main");
        this.apiVersion = values.getOrDefault("api-version", ApiVersion.CURRENT);
        this.description = values.getOrDefault("description", "");
        this.author = values.getOrDefault("author", "");
        this.load = parseLoad(values.get("load"));
        this.depend = list(values, lists, "depend");
        this.softDepend = list(values, lists, "softdepend");
        this.loadBefore = list(values, lists, "loadbefore");
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public PluginLoadOrder getLoadOrder() {
        return load;
    }

    public List<String> getDepend() {
        return depend;
    }

    public List<String> getSoftDepend() {
        return softDepend;
    }

    public List<String> getLoadBefore() {
        return loadBefore;
    }

    /** Reads plugin.yml from a file, wrapping IO failures so the loader can report them. */
    public static PluginDescription fromFile(Path path) {
        try {
            return parse(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new PluginDescriptionException("Could not read " + path + ": " + e.getMessage(), e);
        }
    }

    /** Parses the text of a plugin.yml. */
    public static PluginDescription parse(String content) {
        Map<String, String> values = new LinkedHashMap<>();
        Map<String, List<String>> lists = new LinkedHashMap<>();
        String currentList = null;
        for (String line : content.split("\\R")) {
            String withoutComment = withoutComment(line);
            if (withoutComment.isBlank()) {
                continue;
            }
            String text = withoutComment.strip();
            int indent = withoutComment.length() - withoutComment.stripLeading().length();
            if (text.startsWith("- ") && indent > 0 && currentList != null) {
                lists.computeIfAbsent(currentList, key -> new ArrayList<>()).add(unquote(text.substring(2).strip()));
                continue;
            }
            int colon = text.indexOf(':');
            if (colon < 0) {
                throw new PluginDescriptionException("plugin.yml line isn't \"key: value\": " + line);
            }
            String key = text.substring(0, colon).strip();
            String value = text.substring(colon + 1).strip();
            if (indent > 0) {
                // Nested maps (such as plugin.yml's commands block) belong to a later phase.
                continue;
            }
            if (value.startsWith("[") && value.endsWith("]")) {
                List<String> inline = new ArrayList<>();
                String body = value.substring(1, value.length() - 1).strip();
                if (!body.isEmpty()) {
                    for (String item : body.split(",")) {
                        inline.add(unquote(item.strip()));
                    }
                }
                lists.put(key, inline);
                currentList = null;
            } else {
                values.put(key, unquote(value));
                currentList = value.isEmpty() ? key : null;
            }
        }
        return new PluginDescription(values, lists);
    }

    private static String required(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new PluginDescriptionException("plugin.yml is missing the required \"" + key + "\" field");
        }
        return value;
    }

    private static PluginLoadOrder parseLoad(String value) {
        if (value == null || value.isBlank()) {
            return PluginLoadOrder.POSTWORLD;
        }
        try {
            return PluginLoadOrder.valueOf(value.strip().toUpperCase(java.util.Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new PluginDescriptionException("plugin.yml load must be STARTUP or POSTWORLD, not \""
                    + value + "\"");
        }
    }

    private static List<String> list(Map<String, String> values, Map<String, List<String>> lists,
                                     String key) {
        List<String> fromLists = lists.get(key);
        if (fromLists != null) {
            return List.copyOf(fromLists);
        }
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(unquote(value));
    }

    /** A # that starts the line or follows whitespace begins a comment, outside quotes. */
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
        if (value.length() >= 2 && value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1).replace("''", "'");
        }
        return value;
    }
}
