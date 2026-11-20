import com.netherrack.api.ApiVersion;
import com.netherrack.api.Server;
import com.netherrack.api.plugin.Plugin;
import com.netherrack.api.plugin.PluginBase;
import com.netherrack.api.plugin.PluginConfig;
import com.netherrack.api.plugin.PluginDescription;
import com.netherrack.api.plugin.PluginDescriptionException;
import com.netherrack.api.plugin.PluginLoadOrder;
import com.netherrack.api.plugin.PluginLogger;
import com.netherrack.api.plugin.PluginManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** Plain-Java checks for the lifecycle layer. Run with tests/run.sh. */
public final class PluginApiTest {

    private static int failures;

    public static void main(String[] args) throws Exception {
        versions();
        descriptionParsing();
        lifecycle();
        configRoundTrip();

        System.out.println(failures == 0 ? "All plugin API checks passed." : failures + " check(s) failed.");
        System.exit(failures == 0 ? 0 : 1);
    }

    private static void versions() {
        check("current version", ApiVersion.CURRENT, "0.4.0");
        check("same version compatible", ApiVersion.isCompatible("0.4.0"), true);
        check("newer patch compatible", ApiVersion.isCompatible("0.4.7"), true);
        check("snapshot compatible", ApiVersion.isCompatible("0.4.0-SNAPSHOT"), true);
        check("older minor incompatible", ApiVersion.isCompatible("0.3.9"), false);
        check("major incompatible", ApiVersion.isCompatible("1.0.0"), false);
        check("missing version incompatible", ApiVersion.isCompatible(null), false);
        check("garbage incompatible", ApiVersion.isCompatible("banana"), false);
    }

    private static void descriptionParsing() {
        PluginDescription description = PluginDescription.parse("""
                # A test plugin
                name: ExamplePlugin
                version: 1.2.3
                api-version: 0.4.0
                main: com.example.ExamplePlugin
                description: "An example plugin"
                author: 'Someone'
                load: STARTUP
                depend: [First, Second]
                softdepend:
                  - Optional
                  - Another
                """);
        check("name", description.getName(), "ExamplePlugin");
        check("version", description.getVersion(), "1.2.3");
        check("api version", description.getApiVersion(), "0.4.0");
        check("main class", description.getMain(), "com.example.ExamplePlugin");
        check("description", description.getDescription(), "An example plugin");
        check("author", description.getAuthor(), "Someone");
        check("load order", description.getLoadOrder(), PluginLoadOrder.STARTUP);
        check("inline list", description.getDepend(), List.of("First", "Second"));
        check("indented list", description.getSoftDepend(), List.of("Optional", "Another"));
        check("absent list", description.getLoadBefore(), List.of());

        PluginDescription defaults = PluginDescription.parse("name: X\nversion: 1\nmain: X\n");
        check("default load order", defaults.getLoadOrder(), PluginLoadOrder.POSTWORLD);
        check("default api version", defaults.getApiVersion(), ApiVersion.CURRENT);

        check("missing main rejected", throwsDescription("name: X\nversion: 1\n"), true);
        check("bad load order rejected", throwsDescription("name: X\nversion: 1\nmain: X\nload: LATE\n"), true);
        check("bad name rejected",
                throwsDescription("name: bad name\nversion: 1\nmain: X\n"), true);
    }

    private static void lifecycle() throws Exception {
        Path dir = Files.createTempDirectory("netherrack-api-test");
        PluginDescription description = PluginDescription.parse("""
                name: LifecyclePlugin
                version: 1.0.0
                api-version: 0.4.0
                main: LifecyclePlugin
                """);
        RecordingLogger logger = new RecordingLogger();
        StubServer server = new StubServer(logger);
        StubManager manager = new StubManager();
        TestPlugin plugin = new TestPlugin();

        plugin.initialize(manager, server, description, dir.resolve("LifecyclePlugin"), logger,
                PluginApiTest.class.getClassLoader());

        check("enabled starts false", plugin.isEnabled(), false);
        plugin.setEnabled(true);
        check("enabled set", plugin.isEnabled(), true);
        check("name from description", plugin.getName(), "LifecyclePlugin");
        check("data folder created", Files.isDirectory(plugin.getDataFolder()), true);
        check("config path", plugin.getConfig().getFile(),
                plugin.getDataFolder().resolve("config.yml"));
        check("server injected", plugin.getServer(), server);
        check("manager injected", plugin.getPluginManager(), manager);
        check("logger injected", plugin.getLogger(), logger);

        plugin.onLoad();
        plugin.onEnable();
        plugin.onDisable();
        check("lifecycle order", plugin.calls, List.of("load", "enable", "disable"));
        check("no lifecycle log noise", logger.lines.isEmpty(), true);
    }

    private static void configRoundTrip() throws Exception {
        Path dir = Files.createTempDirectory("netherrack-api-config");
        Path file = dir.resolve("config.yml");
        PluginConfig config = new PluginConfig(file);
        config.set("greeting", "hello world");
        config.set("database.host", "localhost");
        config.set("database.port", 25565);
        config.set("enabled", true);
        config.save();

        PluginConfig reread = new PluginConfig(file);
        reread.load();
        check("string value", reread.get("greeting", "?"), "hello world");
        check("nested value", reread.get("database.host", "?"), "localhost");
        check("int value", reread.getInt("database.port", 0), 25565);
        check("boolean value", reread.getBoolean("enabled", false), true);
        check("fallback", reread.get("missing", "fallback"), "fallback");
        check("keys", reread.keys().size(), 4);

        Files.writeString(file, "section:\n  key: value\n# comment\nplain: 7\n");
        PluginConfig nested = new PluginConfig(file);
        nested.load();
        check("indented key", nested.get("section.key", "?"), "value");
        check("top level key", nested.get("plain", "?"), "7");
    }

    private static boolean throwsDescription(String yaml) {
        try {
            PluginDescription.parse(yaml);
            return false;
        } catch (PluginDescriptionException e) {
            return true;
        }
    }

    private static void check(String name, Object actual, Object expected) {
        if (!Objects.equals(actual, expected)) {
            failures++;
            System.out.println("FAIL " + name + ": expected <" + expected + "> but got <" + actual + ">");
        } else {
            System.out.println("ok   " + name);
        }
    }

    private static final class TestPlugin extends PluginBase {
        final List<String> calls = new ArrayList<>();

        @Override
        public void onLoad() {
            calls.add("load");
        }

        @Override
        public void onEnable() {
            calls.add("enable");
        }

        @Override
        public void onDisable() {
            calls.add("disable");
        }
    }

    private static final class RecordingLogger implements PluginLogger {
        final List<String> lines = new ArrayList<>();

        @Override
        public void info(String message) {
            lines.add(message);
        }

        @Override
        public void warn(String message) {
            lines.add(message);
        }

        @Override
        public void error(String message) {
            lines.add(message);
        }

        @Override
        public void debug(String message) {
            lines.add(message);
        }
    }

    private static final class StubServer implements Server {
        private final PluginLogger logger;

        StubServer(PluginLogger logger) {
            this.logger = logger;
        }

        @Override
        public String getName() {
            return "Netherrack";
        }

        @Override
        public String getVersion() {
            return "0.4.0-beta";
        }

        @Override
        public PluginManager getPluginManager() {
            return null;
        }

        @Override
        public PluginLogger getLogger() {
            return logger;
        }
    }

    private static final class StubManager implements PluginManager {
        @Override
        public Plugin getPlugin(String name) {
            return null;
        }

        @Override
        public Collection<Plugin> getPlugins() {
            return List.of();
        }

        @Override
        public boolean isPluginEnabled(String name) {
            return false;
        }
    }
}
