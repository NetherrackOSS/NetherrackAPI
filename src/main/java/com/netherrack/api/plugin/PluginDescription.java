package com.netherrack.api.plugin;

import com.netherrack.api.ApiVersion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The contents of a plugin's plugin.yml.
 * <p>
 * Only the YAML this needs is understood: top-level "key: value" lines, lists written either
 * inline as "[a, b]" or as indented "- a" lines, and the nested {@code commands:} block. Values may
 * be quoted, and # starts a comment outside quotes. Keeping the reader here rather than adding a
 * YAML dependency means plugin jars stay small and the API keeps its promise of having no
 * dependencies. Unknown keys and unknown nested sections are ignored, so a plugin can carry settings
 * for a newer server without failing on an older one.
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
    private final List<CommandDescription> commands;

    private PluginDescription(Map<String, String> values, Map<String, List<String>> lists,
                              List<CommandDescription> commands) {
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
        this.commands = commands;
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

    /** The commands declared in plugin.yml, in the order they were written. */
    public List<CommandDescription> getCommands() {
        return commands;
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
        Map<String, CommandBuilder> commandBuilders = new LinkedHashMap<>();
        String section = null;
        String currentTopList = null;
        String currentCommand = null;
        String commandList = null;

        for (String line : content.split("\\R")) {
            String withoutComment = withoutComment(line);
            if (withoutComment.isBlank()) {
                continue;
            }
            String text = withoutComment.strip();
            int indent = withoutComment.length() - withoutComment.stripLeading().length();

            if (text.startsWith("- ") && indent > 0) {
                if (currentTopList != null) {
                    lists.computeIfAbsent(currentTopList, key -> new ArrayList<>())
                            .add(unquote(text.substring(2).strip()));
                } else if (commandList != null && currentCommand != null) {
                    commandBuilders.get(currentCommand).aliases.add(unquote(text.substring(2).strip()));
                }
                continue;
            }

            int colon = text.indexOf(':');
            if (colon < 0) {
                throw new PluginDescriptionException("plugin.yml line isn't \"key: value\": " + line);
            }
            String key = text.substring(0, colon).strip();
            String value = text.substring(colon + 1).strip();

            if (indent == 0) {
                section = value.isEmpty() ? key : null;
                currentTopList = value.isEmpty() ? key : null;
                currentCommand = null;
                commandList = null;
                if (value.isEmpty()) {
                    values.putIfAbsent(key, "");
                } else if (value.startsWith("[") && value.endsWith("]")) {
                    lists.put(key, inlineList(value));
                } else {
                    values.put(key, unquote(value));
                }
                continue;
            }

            if (!"commands".equals(section)) {
                continue;
            }
            if (indent <= 2) {
                if (commandBuilders.containsKey(key)) {
                    throw new PluginDescriptionException("duplicate command \"" + key + "\" in plugin.yml");
                }
                CommandBuilder builder = new CommandBuilder(key);
                if (!value.isEmpty()) {
                    builder.description = unquote(value);
                }
                commandBuilders.put(key, builder);
                currentCommand = key;
                currentTopList = null;
                commandList = null;
                continue;
            }
            if (currentCommand == null) {
                continue;
            }
            CommandBuilder builder = commandBuilders.get(currentCommand);
            switch (key) {
                case "description" -> {
                    builder.description = unquote(value);
                    commandList = null;
                }
                case "usage" -> {
                    builder.usage = unquote(value);
                    commandList = null;
                }
                case "permission" -> {
                    builder.permission = unquote(value);
                    commandList = null;
                }
                case "aliases" -> {
                    commandList = value.isEmpty() ? "aliases" : null;
                    if (!value.isEmpty()) {
                        builder.aliases.addAll(inlineList(value));
                    }
                }
                default -> commandList = null;
            }
        }

        List<CommandDescription> commands = new ArrayList<>();
        for (CommandBuilder builder : commandBuilders.values()) {
            commands.add(builder.build());
        }
        return new PluginDescription(values, lists, List.copyOf(commands));
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
            return PluginLoadOrder.valueOf(value.strip().toUpperCase(Locale.ROOT).replace('-', '_'));
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

    private static List<String> inlineList(String value) {
        List<String> result = new ArrayList<>();
        String body = value.substring(1, value.length() - 1).strip();
        if (!body.isEmpty()) {
            for (String item : body.split(",")) {
                result.add(unquote(item.strip()));
            }
        }
        return result;
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

    /** Collects the fields of one command before validating it into a CommandDescription. */
    private static final class CommandBuilder {

        private final String name;
        private String description = "";
        private String usage = "";
        private String permission = "";
        private final List<String> aliases = new ArrayList<>();

        private CommandBuilder(String name) {
            this.name = name;
        }

        private CommandDescription build() {
            return new CommandDescription(name, description, aliases, usage, permission);
        }
    }
}
