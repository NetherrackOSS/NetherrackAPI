import com.netherrack.api.ApiVersion;
import com.netherrack.api.Server;
import com.netherrack.api.command.Command;
import com.netherrack.api.command.CommandSender;
import com.netherrack.api.command.PluginCommand;
import com.netherrack.api.entity.DamageCause;
import com.netherrack.api.entity.GameMode;
import com.netherrack.api.entity.OfflinePlayer;
import com.netherrack.api.entity.Player;
import com.netherrack.api.entity.TeleportCause;
import com.netherrack.api.event.Event;
import com.netherrack.api.event.EventException;
import com.netherrack.api.event.EventHandler;
import com.netherrack.api.event.EventManager;
import com.netherrack.api.event.EventPriority;
import com.netherrack.api.event.Listener;
import com.netherrack.api.event.block.BlockPlaceEvent;
import com.netherrack.api.event.player.PlayerActionBarEvent;
import com.netherrack.api.event.player.PlayerChatEvent;
import com.netherrack.api.event.player.PlayerDamageEvent;
import com.netherrack.api.event.player.PlayerDeathEvent;
import com.netherrack.api.event.player.PlayerEvent;
import com.netherrack.api.event.player.PlayerFormRespondedEvent;
import com.netherrack.api.event.player.PlayerGameModeChangeEvent;
import com.netherrack.api.event.player.PlayerInventoryClickEvent;
import com.netherrack.api.event.player.PlayerItemConsumeEvent;
import com.netherrack.api.event.player.PlayerItemDropEvent;
import com.netherrack.api.event.player.PlayerItemPickupEvent;
import com.netherrack.api.event.player.PlayerItemUseEvent;
import com.netherrack.api.event.player.PlayerJoinEvent;
import com.netherrack.api.event.player.PlayerMetadataChangeEvent;
import com.netherrack.api.event.player.PlayerMoveEvent;
import com.netherrack.api.event.player.PlayerPopupEvent;
import com.netherrack.api.event.player.PlayerQuitEvent;
import com.netherrack.api.event.player.PlayerRespawnEvent;
import com.netherrack.api.event.player.PlayerTeleportEvent;
import com.netherrack.api.event.player.PlayerToastEvent;
import com.netherrack.api.form.Form;
import com.netherrack.api.form.FormButtonImage;
import com.netherrack.api.form.SimpleForm;
import com.netherrack.api.form.SimpleFormResponse;
import com.netherrack.api.item.InventoryClickAction;
import com.netherrack.api.item.ItemStack;
import com.netherrack.api.permission.Permission;
import com.netherrack.api.permission.PermissionAttachment;
import com.netherrack.api.permission.PermissionDefault;
import com.netherrack.api.permission.PermissionManager;
import com.netherrack.api.permission.Permissible;
import com.netherrack.api.permission.PermissionState;
import com.netherrack.api.plugin.Plugin;
import com.netherrack.api.plugin.PluginBase;
import com.netherrack.api.plugin.PluginConfig;
import com.netherrack.api.plugin.PluginDescription;
import com.netherrack.api.plugin.PluginDescriptionException;
import com.netherrack.api.plugin.PluginLoadOrder;
import com.netherrack.api.plugin.PluginLogger;
import com.netherrack.api.plugin.PluginManager;
import com.netherrack.api.scheduler.Scheduler;
import com.netherrack.api.scheduler.TaskHandler;
import com.netherrack.api.world.BlockPosition;
import com.netherrack.api.world.BlockSnapshot;
import com.netherrack.api.world.Location;
import com.netherrack.api.world.World;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Plain-Java checks for the lifecycle and event layers. Run with tests/run.sh. */
public final class PluginApiTest {

    private static int failures;

    public static void main(String[] args) throws Exception {
        versions();
        descriptionParsing();
        permissions();
        commandDeclarations();
        worldTypes();
        lifecycle();
        configRoundTrip();
        events();
        playerEvents();
        forms();

        System.out.println(failures == 0 ? "All plugin API checks passed." : failures + " check(s) failed.");
        System.exit(failures == 0 ? 0 : 1);
    }

    private static void versions() {
        check("current version", ApiVersion.CURRENT, "0.6.0");
        check("current version compatible", ApiVersion.isCompatible("0.6.0"), true);
        check("newer patch compatible", ApiVersion.isCompatible("0.6.7"), true);
        check("snapshot compatible", ApiVersion.isCompatible("0.6.0-SNAPSHOT"), true);
        check("older minor incompatible", ApiVersion.isCompatible("0.5.9"), false);
        check("major incompatible", ApiVersion.isCompatible("1.0.0"), false);
        check("missing version incompatible", ApiVersion.isCompatible(null), false);
        check("garbage incompatible", ApiVersion.isCompatible("banana"), false);
    }

    private static void descriptionParsing() {
        PluginDescription description = PluginDescription.parse("""
                # A test plugin
                name: ExamplePlugin
                version: 1.2.3
                api-version: 0.5.0
                main: com.example.ExamplePlugin
                description: "An example plugin"
                author: 'Someone'
                load: STARTUP
                depend: [First, Second]
                softdepend:
                  - Optional
                  - Another
                commands:
                  hello:
                    description: Says hello
                    aliases: [hi, greet]
                    usage: /hello [name]
                    permission: example.hello
                  ping: Pongs back
                """);
        check("name", description.getName(), "ExamplePlugin");
        check("version", description.getVersion(), "1.2.3");
        check("api version", description.getApiVersion(), "0.5.0");
        check("main class", description.getMain(), "com.example.ExamplePlugin");
        check("description", description.getDescription(), "An example plugin");
        check("author", description.getAuthor(), "Someone");
        check("load order", description.getLoadOrder(), PluginLoadOrder.STARTUP);
        check("inline list", description.getDepend(), List.of("First", "Second"));
        check("indented list", description.getSoftDepend(), List.of("Optional", "Another"));
        check("absent list", description.getLoadBefore(), List.of());
        check("command count", description.getCommands().size(), 2);
        check("first command", description.getCommands().get(0).getName(), "hello");
        check("command description", description.getCommands().get(0).getDescription(), "Says hello");
        check("command aliases", description.getCommands().get(0).getAliases(), List.of("hi", "greet"));
        check("command usage", description.getCommands().get(0).getUsage(), "/hello [name]");
        check("command permission", description.getCommands().get(0).getPermission(), "example.hello");
        check("compact command", description.getCommands().get(1).getDescription(), "Pongs back");
        check("default usage", description.getCommands().get(1).getUsage(), "/ping");

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
                api-version: 0.5.0
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

    private static void events() {
        List<String> errors = new ArrayList<>();
        EventManager manager = new EventManager(error -> errors.add(describe(error)));
        StubPlayer player = new StubPlayer();
        StubPlugin plugin = new StubPlugin();

        PriorityListener priority = new PriorityListener();
        manager.registerEvents(priority, plugin);
        manager.callEvent(new PlayerJoinEvent(player));
        check("priority and base class order", priority.calls,
                List.of("lowest", "high", "monitor"));

        CancellationListener cancellation = new CancellationListener();
        manager.registerEvents(cancellation, plugin);
        PlayerChatEvent chat = new PlayerChatEvent(player, "original");
        manager.callEvent(chat);
        check("cancellation order", cancellation.calls,
                List.of("canceller", "monitor:changed:true"));
        check("cancelled event stays cancelled", chat.isCancelled(), true);
        check("chat message changed", chat.getMessage(), "changed");

        FailingListener failing = new FailingListener();
        manager.registerEvents(failing, plugin);
        manager.callEvent(new PlayerQuitEvent(player));
        check("later handler survives failure", failing.calls, List.of("after failure"));
        check("handler failure reported", errors.size(), 1);
        check("handler failure keeps cause", errors.get(0).contains("boom"), true);

        StubManager visibleManager = new StubManager();
        JoinListener join = new JoinListener();
        visibleManager.registerEvents(join, plugin);
        visibleManager.callEvent(new PlayerJoinEvent(player));
        check("plugin manager dispatches", join.calls, List.of(1));
        visibleManager.unregisterEvents(plugin);
        visibleManager.callEvent(new PlayerJoinEvent(player));
        check("plugin unregister removes handlers", join.calls, List.of(1));

        BlockSnapshot stone = new BlockSnapshot("minecraft:stone", 12345);
        BlockPlaceEvent place = new BlockPlaceEvent(player, new BlockPosition(1, 64, -3), stone);
        place.setCancelled(true);
        check("event name", place.getEventName(), "BlockPlaceEvent");
        check("event block position", place.getPosition().toString(), "(1, 64, -3)");
        check("event block snapshot", place.getBlock(), stone);
        check("block place cancellation", place.isCancelled(), true);

        PlayerActionBarEvent actionBar = new PlayerActionBarEvent(player, "bar", 1, 0, 1);
        actionBar.setMessage("changed bar");
        actionBar.setFadeInTime(2);
        actionBar.setStayTime(20);
        actionBar.setFadeOutTime(3);
        check("action bar event name", actionBar.getEventName(), "PlayerActionBarEvent");
        check("action bar message changed", actionBar.getMessage(), "changed bar");
        check("action bar times changed",
                List.of(actionBar.getFadeInTime(), actionBar.getStayTime(),
                        actionBar.getFadeOutTime()), List.of(2, 20, 3));
        check("action bar starts uncancelled", actionBar.isCancelled(), false);
        actionBar.setCancelled(true);
        check("action bar cancellation", actionBar.isCancelled(), true);

        PlayerToastEvent toast = new PlayerToastEvent(player, "title", "content");
        toast.setTitle("changed title");
        toast.setContent("changed content");
        check("toast event name", toast.getEventName(), "PlayerToastEvent");
        check("toast title changed", toast.getTitle(), "changed title");
        check("toast content changed", toast.getContent(), "changed content");
        check("toast cancellation", toast.isCancelled(), false);

        PlayerPopupEvent popup = new PlayerPopupEvent(player, "popup");
        popup.setMessage("changed popup");
        check("popup event name", popup.getEventName(), "PlayerPopupEvent");
        check("popup message changed", popup.getMessage(), "changed popup");
        popup.setCancelled(true);
        check("popup cancellation", popup.isCancelled(), true);

        StubManager invalid = new StubManager();
        check("invalid handler rejected", throwsEventException(() ->
                invalid.registerEvents(new InvalidListener(), plugin)), true);

        PlayerFormRespondedEvent formEvent =
                new PlayerFormRespondedEvent(player, null, 7, "0", false);
        check("form event name", formEvent.getEventName(), "PlayerFormRespondedEvent");
        check("form event id", formEvent.getFormId(), 7);
        check("form event response", formEvent.getResponse(), "0");
        check("form event closed", formEvent.wasClosed(), false);
    }

    private static void playerEvents() {
        StubPlayer player = new StubPlayer();

        PlayerDamageEvent damage = new PlayerDamageEvent(player, DamageCause.LAVA, 4);
        check("damage event name", damage.getEventName(), "PlayerDamageEvent");
        check("damage cause", damage.getCause(), DamageCause.LAVA);
        check("damage amount", damage.getAmount(), 4d);
        check("original damage", damage.getOriginalAmount(), 4d);
        damage.setAmount(-2);
        check("negative damage clamped", damage.getAmount(), 0d);
        damage.setCancelled(true);
        check("damage cancellation", damage.isCancelled(), true);

        PlayerDeathEvent death = new PlayerDeathEvent(player, DamageCause.FALL, "fell",
                List.of(new ItemStack("minecraft:dirt", 2)));
        check("death event name", death.getEventName(), "PlayerDeathEvent");
        check("death cause", death.getCause(), DamageCause.FALL);
        death.setDeathMessage("changed death");
        check("death message changed", death.getDeathMessage(), "changed death");
        death.getDrops().add(new ItemStack("minecraft:apple", 1));
        check("death drops changed", death.getDrops().size(), 2);

        Location spawn = new Location(10, 64, -5);
        PlayerRespawnEvent respawn = new PlayerRespawnEvent(player, spawn);
        respawn.setRespawnLocation(new Location(1, 2, 3));
        check("respawn location", respawn.getRespawnLocation(), new Location(1, 2, 3));

        Location from = new Location(0, 64, 0);
        Location to = new Location(1, 64, 1);
        PlayerMoveEvent move = new PlayerMoveEvent(player, from, to);
        check("move from", move.getFrom(), from);
        move.setTo(new Location(2, 64, 2));
        check("move destination changed", move.getTo(), new Location(2, 64, 2));
        move.setCancelled(true);
        check("move cancellation", move.isCancelled(), true);

        PlayerTeleportEvent teleport = new PlayerTeleportEvent(player, from, to,
                TeleportCause.COMMAND);
        check("teleport is a move", teleport instanceof PlayerMoveEvent, true);
        check("teleport cause", teleport.getCause(), TeleportCause.COMMAND);
        check("teleport event name", teleport.getEventName(), "PlayerTeleportEvent");

        PlayerGameModeChangeEvent mode = new PlayerGameModeChangeEvent(player, GameMode.SURVIVAL,
                GameMode.CREATIVE);
        check("game mode from", mode.getFrom(), GameMode.SURVIVAL);
        check("game mode to", mode.getTo(), GameMode.CREATIVE);
        mode.setCancelled(true);
        check("game mode cancellation", mode.isCancelled(), true);

        ItemStack apple = new ItemStack("minecraft:apple", 1);
        PlayerItemPickupEvent pickup = new PlayerItemPickupEvent(player, apple);
        pickup.setCancelled(true);
        check("pickup event name", pickup.getEventName(), "PlayerItemPickupEvent");
        check("pickup cancellation", pickup.isCancelled(), true);
        PlayerItemDropEvent drop = new PlayerItemDropEvent(player, apple);
        drop.setItem(new ItemStack("minecraft:dirt", 3));
        check("drop item changed", drop.getItem().getId(), "minecraft:dirt");
        PlayerItemUseEvent use = new PlayerItemUseEvent(player, apple, 3, false,
                new BlockPosition(1, 2, 3));
        check("item use slot", use.getSlot(), 3);
        check("item use position", use.getBlockPosition(), new BlockPosition(1, 2, 3));
        use.setCancelled(true);
        check("item use cancellation", use.isCancelled(), true);
        PlayerInventoryClickEvent click = new PlayerInventoryClickEvent(player, 5, apple,
                new ItemStack("minecraft:air", 0), InventoryClickAction.TAKE);
        check("inventory click slot", click.getSlot(), 5);
        check("inventory click action", click.getAction(), InventoryClickAction.TAKE);
        click.setCancelled(true);
        check("inventory click cancellation", click.isCancelled(), true);
        PlayerItemConsumeEvent consume = new PlayerItemConsumeEvent(player, apple);
        consume.setItem(new ItemStack("minecraft:bowl", 1));
        check("consume item changed", consume.getItem().getId(), "minecraft:bowl");
        consume.setCancelled(true);
        check("consume cancellation", consume.isCancelled(), true);

        player.setMeta("Prefix", "[Admin]");
        check("metadata get", player.getPrefix(), "[Admin]");
        check("metadata snapshot", player.getMeta().get("prefix"), "[Admin]");
        player.removeMeta("prefix");
        check("metadata removed", player.getPrefix(), null);
        PlayerMetadataChangeEvent metadata = new PlayerMetadataChangeEvent(player, "suffix", "old",
                "new");
        metadata.setNewValue("changed");
        check("metadata change value", metadata.getNewValue(), "changed");
        metadata.setCancelled(true);
        check("metadata cancellation", metadata.isCancelled(), true);

        StubServer server = new StubServer(new RecordingLogger());
        check("offline player by name", server.getOfflinePlayer("tester").getName(), "Tester");
        check("offline player by xuid", server.getOfflinePlayer("xuid").getXuid(), "xuid");
        check("offline player missing", server.getOfflinePlayer("nobody"), null);
        check("offline players", server.getOfflinePlayers().size(), 1);
    }

    private static void forms() {
        StubPlayer player = new StubPlayer();
        List<String> calls = new ArrayList<>();
        SimpleForm form = new SimpleForm("Test \"quoted\"", "Line\nTwo")
                .addButton("Test")
                .addButton("Other")
                .onSubmit((submitter, response) -> calls.add("submit:" + response.getButtonId()
                        + ":" + response.getButtonText() + ":" + (submitter == player)))
                .onClose(closer -> calls.add("close:" + (closer == player)));

        String json = form.toJson();
        check("form json type", json.contains("\"type\":\"form\""), true);
        check("form json title", json.contains("\"title\":\"Test \\\"quoted\\\"\""), true);
        check("form json content", json.contains("\"content\":\"Line\\nTwo\""), true);
        check("form json buttons", json.contains("\"text\":\"Test\"")
                && json.contains("\"text\":\"Other\""), true);

        SimpleForm icons = new SimpleForm("Icons", "Pick one")
                .addButton("Apple", FormButtonImage.path("textures/items/apple"))
                .addButton("Website", FormButtonImage.url("https://example.com/button.png"))
                .addButton("Plain")
                .addButton("Empty", FormButtonImage.path(""));
        String iconJson = icons.toJson();
        check("path button image", iconJson.contains(
                "\"image\":{\"type\":\"path\",\"data\":\"textures/items/apple\"}"), true);
        check("url button image", iconJson.contains(
                "\"image\":{\"type\":\"url\",\"data\":\"https://example.com/button.png\"}"), true);
        check("plain button has no image", iconJson.contains(
                "{\"type\":\"button\",\"text\":\"Plain\"}"), true);
        check("empty button image ignored", iconJson.contains(
                "{\"type\":\"button\",\"text\":\"Empty\"}"), true);
        check("button labels", icons.getButtons(),
                List.of("Apple", "Website", "Plain", "Empty"));
        check("button image lookup", icons.getButtonImage(1).getData(),
                "https://example.com/button.png");
        check("missing button image", icons.getButtonImage(2), null);
        check("empty image lookup", icons.getButtonImage(3), null);
        check("image constructor", new FormButtonImage(FormButtonImage.TYPE_PATH, "icon").getType(),
                FormButtonImage.TYPE_PATH);

        form.handleResponse(player, "1", false);
        form.handleResponse(player, "null", false);
        form.handleResponse(player, "bad", false);
        check("form callbacks", calls,
                List.of("submit:1:Other:true", "close:true", "close:true"));

        check("player show form", player.showForm(form), 7);
        check("player shown form", player.shownForm(), form);
        check("second form refused", player.showForm(new SimpleForm("Other", "Other")), -1);
    }

    private static void permissions() {
        StubPlugin plugin = new StubPlugin();
        StubPermissionManager registry = new StubPermissionManager();
        Permission node = new Permission("example.hello", "Use /hello", PermissionDefault.OPERATOR,
                plugin);
        registry.registerPermission(node);
        check("registered permission", registry.getPermission("EXAMPLE.HELLO"), node);
        check("permission default", node.getDefault(), PermissionDefault.OPERATOR);

        TestPermissible subject = new TestPermissible();
        PermissionAttachment attachment = subject.addAttachment(plugin);
        check("attachment owner", attachment.getPlugin(), plugin);
        check("undefined by default", subject.getPermissionValue("example.hello"), PermissionState.UNDEFINED);
        check("undefined means no permission", subject.hasPermission("example.hello"), false);

        attachment.setPermission("example.hello", true);
        check("attachment allows", subject.getPermissionValue("example.hello"), PermissionState.ALLOW);
        check("allow checked", subject.hasPermission("example.hello"), true);
        attachment.setPermission("example.hello", false);
        check("attachment denies", subject.getPermissionValue("example.hello"), PermissionState.DENY);
        check("deny checked", subject.hasPermission("example.hello"), false);
        attachment.unsetPermission("example.hello");
        check("unset returns to undefined", subject.getPermissionValue("EXAMPLE.HELLO"),
                PermissionState.UNDEFINED);

        attachment.remove();
        check("removed attachment", attachment.isRemoved(), true);
        check("removed attachment detached", subject.attachments.isEmpty(), true);
        check("using removed attachment rejected", throwsIllegalState(() ->
                attachment.setPermission("example.hello", true)), true);

        registry.unregisterPermissions(plugin);
        check("unregister by plugin", registry.getPermission("example.hello"), null);
    }

    private static void commandDeclarations() {
        StubPlugin plugin = new StubPlugin();
        PluginDescription desc = PluginDescription.parse("""
                name: CommandPlugin
                version: 1
                main: CommandPlugin
                commands:
                  hello:
                    description: Says hello
                    aliases: [hi]
                    usage: /hello [name]
                    permission: example.hello
                """);
        PluginCommand command = new PluginCommand(plugin, desc.getCommands().get(0));
        check("command name", command.getName(), "hello");
        check("command aliases", command.getAliases(), List.of("hi"));
        check("command description", command.getDescription(), "Says hello");
        check("command usage", command.getUsage(), "/hello [name]");
        check("command permission", command.getPermission(), "example.hello");
        check("command owner", command.getPlugin(), plugin);

        boolean[] called = {false};
        command.setExecutor((sender, cmd, label, args) -> {
            called[0] = true;
            check("executor gets label", label, "hi");
            return true;
        });
        check("executor result", command.execute(null, "hi", new String[0]), true);
        check("executor called", called[0], true);

        command.setExecutor(null);
        check("missing executor is not handled", command.execute(null, "hello", new String[0]), false);
    }

    private static void worldTypes() {
        Location location = new Location(3.8, -2.3, 9.9, 90f, -12f);
        check("location x", location.getX(), 3.8);
        check("location y", location.getY(), -2.3);
        check("location z", location.getZ(), 9.9);
        check("location yaw", location.getYaw(), 90f);
        check("location pitch", location.getPitch(), -12f);
        BlockPosition block = location.toBlockPosition();
        check("location block x", block.getX(), 3);
        check("location block y", block.getY(), -3);
        check("location block z", block.getZ(), 9);

        check("game mode id", GameMode.SPECTATOR.getId(), 6);
        check("game mode lookup", GameMode.fromId(2), GameMode.ADVENTURE);
        check("unknown game mode", GameMode.fromId(99), null);

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("FACING", "north");
        properties.put("top_slot_bit", false);
        BlockSnapshot stair = new BlockSnapshot("minecraft:oak_stairs", 123, properties);
        check("snapshot state", stair.getState("facing"), "north");
        check("snapshot bit", stair.getState("TOP_SLOT_BIT"), false);
        check("snapshot state count", stair.getStates().size(), 2);
        check("snapshot states immutable", throwsUnsupported(
                () -> stair.getStates().put("waterlogged", true)), true);
        check("snapshot without states", new BlockSnapshot("minecraft:stone", 1).getStates().isEmpty(), true);

        StubPlayer player = new StubPlayer();
        Location destination = new Location(10, 64, -5, 180f, 0f);
        player.teleport(destination);
        check("player teleport", player.getLocation(), destination);
        player.setGameMode(GameMode.CREATIVE);
        check("player game mode", player.getGameMode(), GameMode.CREATIVE);
    }

    private static boolean throwsUnsupported(Runnable action) {
        try {
            action.run();
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    private static boolean throwsIllegalState(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    private static boolean throwsDescription(String yaml) {
        try {
            PluginDescription.parse(yaml);
            return false;
        } catch (PluginDescriptionException e) {
            return true;
        }
    }

    private static boolean throwsEventException(Runnable action) {
        try {
            action.run();
            return false;
        } catch (EventException e) {
            return true;
        }
    }

    private static String describe(Throwable error) {
        Throwable cause = error.getCause();
        return (cause == null ? error : cause).toString();
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

    private static final class StubPlugin extends PluginBase {
    }

    private static final class PriorityListener implements Listener {
        final List<String> calls = new ArrayList<>();

        @EventHandler(priority = EventPriority.HIGH)
        public void high(PlayerJoinEvent event) {
            calls.add("high");
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void lowest(PlayerJoinEvent event) {
            calls.add("lowest");
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void monitor(PlayerEvent event) {
            calls.add("monitor");
        }
    }

    private static final class CancellationListener implements Listener {
        final List<String> calls = new ArrayList<>();

        @EventHandler(priority = EventPriority.LOWEST)
        public void cancel(PlayerChatEvent event) {
            calls.add("canceller");
            event.setMessage("changed");
            event.setCancelled(true);
        }

        @EventHandler
        public void ignored(PlayerChatEvent event) {
            calls.add("ignored");
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
        public void monitor(PlayerChatEvent event) {
            calls.add("monitor:" + event.getMessage() + ":" + event.isCancelled());
        }
    }

    private static final class FailingListener implements Listener {
        final List<String> calls = new ArrayList<>();

        @EventHandler(priority = EventPriority.LOWEST)
        public void fail(PlayerQuitEvent event) {
            throw new IllegalStateException("boom");
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void after(PlayerQuitEvent event) {
            calls.add("after failure");
        }
    }

    private static final class JoinListener implements Listener {
        final List<Integer> calls = new ArrayList<>();

        @EventHandler
        public void join(PlayerJoinEvent event) {
            calls.add(1);
        }
    }

    private static final class InvalidListener implements Listener {
        @EventHandler
        public void wrongParameters(String notAnEvent) {
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

    private static final class StubPlayer extends TestPermissible implements Player {
        private final Map<String, String> meta = new LinkedHashMap<>();
        private Location location = new Location(0, 64, 0);
        private GameMode gameMode = GameMode.SURVIVAL;
        private Form shownForm;
        private boolean formOpen;

        @Override
        public String getName() {
            return "Tester";
        }

        @Override
        public UUID getUuid() {
            return UUID.nameUUIDFromBytes("Tester".getBytes());
        }

        @Override
        public String getXuid() {
            return "";
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public void teleport(Location location) {
            this.location = location;
        }

        @Override
        public GameMode getGameMode() {
            return gameMode;
        }

        @Override
        public void setGameMode(GameMode gameMode) {
            this.gameMode = gameMode;
        }

        @Override
        public void sendMessage(String message) {
        }

        @Override
        public void sendActionBar(String message) {
        }

        @Override
        public void sendActionBar(String message, int fadeInTime, int stayTime, int fadeOutTime) {
        }

        @Override
        public void sendToast(String title, String content) {
        }

        @Override
        public void sendPopup(String message) {
        }

        @Override
        public int showForm(Form form) {
            if (formOpen) {
                return -1;
            }
            formOpen = true;
            shownForm = form;
            return 7;
        }

        Form shownForm() {
            return shownForm;
        }

        @Override
        public String getMeta(String key) {
            return meta.get(key.toLowerCase(java.util.Locale.ROOT));
        }

        @Override
        public void setMeta(String key, String value) {
            String lower = key.toLowerCase(java.util.Locale.ROOT);
            if (value == null) {
                meta.remove(lower);
            } else {
                meta.put(lower, value);
            }
        }

        @Override
        public void removeMeta(String key) {
            meta.remove(key.toLowerCase(java.util.Locale.ROOT));
        }

        @Override
        public Map<String, String> getMeta() {
            return Map.copyOf(meta);
        }

        @Override
        public void kick(String reason) {
        }
    }

    private static final class StubOfflinePlayer implements OfflinePlayer {
        @Override
        public String getName() {
            return "Tester";
        }

        @Override
        public UUID getUuid() {
            return UUID.nameUUIDFromBytes("Tester".getBytes());
        }

        @Override
        public String getXuid() {
            return "xuid";
        }

        @Override
        public boolean isOnline() {
            return false;
        }

        @Override
        public Player getPlayer() {
            return null;
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
            return "0.5.0-beta";
        }

        @Override
        public PluginManager getPluginManager() {
            return null;
        }

        @Override
        public PluginLogger getLogger() {
            return logger;
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public Collection<Player> getPlayers() {
            return List.of();
        }

        @Override
        public OfflinePlayer getOfflinePlayer(String nameOrXuid) {
            return nameOrXuid != null
                    && (nameOrXuid.equalsIgnoreCase("Tester") || nameOrXuid.equals("xuid"))
                    ? new StubOfflinePlayer() : null;
        }

        @Override
        public OfflinePlayer getOfflinePlayer(UUID uuid) {
            return uuid != null && uuid.equals(new StubOfflinePlayer().getUuid())
                    ? new StubOfflinePlayer() : null;
        }

        @Override
        public Collection<OfflinePlayer> getOfflinePlayers() {
            return List.of(new StubOfflinePlayer());
        }

        @Override
        public Scheduler getScheduler() {
            return new StubScheduler();
        }

        @Override
        public PermissionManager getPermissionManager() {
            return new StubPermissionManager();
        }
    }

    private static final class StubManager implements PluginManager {
        private final EventManager events = new EventManager();

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

        @Override
        public PluginCommand getCommand(String name, Plugin plugin) {
            return null;
        }

        @Override
        public void registerEvents(Listener listener, Plugin plugin) {
            events.registerEvents(listener, plugin);
        }

        @Override
        public void unregisterEvents(Listener listener) {
            events.unregisterEvents(listener);
        }

        @Override
        public void unregisterEvents(Plugin plugin) {
            events.unregisterEvents(plugin);
        }

        @Override
        public void callEvent(Event event) {
            events.callEvent(event);
        }
    }

    /** Just enough permission bookkeeping to exercise PermissionAttachment in the API tests. */
    private static class TestPermissible implements Permissible {

        final List<PermissionAttachment> attachments = new ArrayList<>();
        private final Map<String, PermissionState> values = new LinkedHashMap<>();

        @Override
        public PermissionState getPermissionValue(String node) {
            if (node == null) {
                return PermissionState.UNDEFINED;
            }
            return values.getOrDefault(node.toLowerCase(java.util.Locale.ROOT), PermissionState.UNDEFINED);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin) {
            PermissionAttachment attachment = new PermissionAttachment(this, plugin);
            attachments.add(attachment);
            return attachment;
        }

        @Override
        public void removeAttachment(PermissionAttachment attachment) {
            attachments.remove(attachment);
        }

        @Override
        public void recalculatePermissions() {
            values.clear();
            for (PermissionAttachment attachment : attachments) {
                values.putAll(attachment.getPermissions());
            }
        }
    }

    private static final class StubPermissionManager implements PermissionManager {

        private final Map<String, Permission> permissions = new LinkedHashMap<>();

        @Override
        public Permission getPermission(String node) {
            return node == null ? null : permissions.get(node.toLowerCase(java.util.Locale.ROOT));
        }

        @Override
        public Collection<Permission> getPermissions() {
            return List.copyOf(permissions.values());
        }

        @Override
        public void registerPermission(Permission permission) {
            permissions.put(permission.getKey(), permission);
        }

        @Override
        public void unregisterPermission(Permission permission) {
            permissions.remove(permission.getKey(), permission);
        }

        @Override
        public void unregisterPermissions(Plugin plugin) {
            permissions.values().removeIf(permission -> permission.getPlugin() == plugin);
        }
    }

    /** The API test never ticks, but a Server has to return something for getScheduler(). */
    private static final class StubScheduler implements Scheduler {

        @Override
        public TaskHandler runTask(Plugin plugin, Runnable task) {
            return null;
        }

        @Override
        public TaskHandler runTaskLater(Plugin plugin, Runnable task, long delayTicks) {
            return null;
        }

        @Override
        public TaskHandler runTaskTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
            return null;
        }

        @Override
        public void cancelTask(int taskId) {
        }

        @Override
        public void cancelTasks(Plugin plugin) {
        }
    }
}
