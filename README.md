# NetherrackAPI
[![](https://jitpack.io/v/NetherrackOSS/NetherrackAPI.svg)](https://jitpack.io/#NetherrackOSS/NetherrackAPI)

Plugin API for [Netherrack](https://github.com/NetherrackOSS/Netherrack), an experimental
Minecraft: Bedrock Edition server written in Java 17.

This repository contains the plugin contracts: lifecycle callbacks, listeners and events, small
`Server`, `Player`, `World` and `BlockSnapshot` interfaces, player locations and game modes, plus
commands, a tick scheduler and a permission layer. See `PLUGIN_API_PLAN.md` in the server repository
for the design.

## Using it in a plugin

JitPack builds the artifact directly from a git tag:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.NetherrackOSS</groupId>
        <artifactId>NetherrackAPI</artifactId>
        <version>v0.4.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

`provided` matters: the server already ships the API, so a plugin jar must not bundle its own
copy.

## A minimal plugin

`src/main/resources/plugin.yml`:

```yaml
name: HelloPlugin
version: 1.0.0
api-version: 0.4.0
main: com.example.hello.HelloPlugin
description: Says hello when enabled
author: You
load: POSTWORLD
```

The main class extends `PluginBase` and overrides whichever lifecycle callbacks it needs:

```java
package com.example.hello;

import com.netherrack.api.event.server.ServerStartEvent;
import com.netherrack.api.plugin.PluginBase;

public final class HelloPlugin extends PluginBase {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new HelloListener(), this);
    }
}
```

`onLoad()` runs while the server is still starting, before the world and network exist. Use it
only for setup that doesn't touch them. `onEnable()` runs once the server is ready, and
`onDisable()` on shutdown in reverse load order.

## Events

A listener implements `Listener` and marks its handler methods with `@EventHandler`:

```java
package com.example.hello;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.EventHandler;
import com.netherrack.api.event.Listener;
import com.netherrack.api.event.player.PlayerJoinEvent;
import com.netherrack.api.event.server.ServerStartEvent;

public final class HelloListener implements Listener {

    @EventHandler
    public void onStart(ServerStartEvent event) {
        // The server is ready and plugins have been enabled.
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Welcome, " + player.getName() + "!");
    }
}
```

Priorities run in this order: `LOWEST`, `LOW`, `NORMAL`, `HIGH`, `HIGHEST`, `MONITOR`. Cancellable
events expose `setCancelled(boolean)`; by default handlers with `ignoreCancelled = true` are
skipped once an earlier handler cancels. Listeners for a base class such as `PlayerEvent` receive
all of its subclasses. When a plugin is disabled, all of its handlers are unregistered.

The first events are:

- `ServerStartEvent`, `ServerStopEvent`
- `PlayerLoginEvent`, `PlayerJoinEvent`, `PlayerQuitEvent`, `PlayerChatEvent`,
  `PlayerCommandEvent`
- `BlockPlaceEvent`, `BlockBreakEvent`

Events are delivered synchronously on the thread doing the action: startup events on the main
startup thread, shutdown events on the shutdown thread, and player and block events on that
player's network thread. A handler should return quickly; use the scheduler for delayed work.

## Players, worlds and blocks

`Player.getLocation()` returns a `Location`: x/y/z at the player's feet, plus yaw and pitch in
degrees. `teleport(Location)` moves the player, their loaded chunks and everyone else's view of
them, so a plugin never has to send movement packets itself.

```java
Location here = player.getLocation();
player.teleport(new Location(here.getX(), here.getY() + 10, here.getZ(), here.getYaw(), here.getPitch()));
player.setGameMode(GameMode.CREATIVE);
```

`World.getBlock(x, y, z)` and `setBlock(x, y, z, snapshot)` use block coordinates and return
immutable `BlockSnapshot` values. A snapshot keeps the block's identifier, its vanilla state hash
and its properties, so a plugin can inspect `getState("facing")` or pass the same snapshot back to
`setBlock` without depending on protocol classes. Passing `null` to `setBlock` clears a block to
air. `GameMode` ids match Bedrock's: survival 0, creative 1, adventure 2 and spectator 6.

## Commands

Declare commands in `plugin.yml` and either override `onCommand` on `PluginBase` or give the command
its own executor:

```yaml
commands:
  hello:
    description: Says hello
    aliases: [hi]
    usage: /hello [name]
    permission: hello.use
```

```java
@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    sender.sendMessage("Hello, " + sender.getName() + "!");
    return true;
}
```

Returning `false` shows the usage line. The server checks `permission` before calling the executor,
and unregisters the command when the plugin is disabled.

## Scheduler and permissions

`Server.getScheduler()` runs tasks on the server tick (20 ticks per second). Tasks may be one-shot,
delayed or repeating, and are cancelled with their owning plugin on disable:

```java
getServer().getScheduler().runTaskTimer(this, () -> {
    getLogger().info("One more tick passed.");
}, 20L, 20L);
```

Permissions sit on top of vanilla's op levels. Register a node with a default, then check it through
a player or command sender:

```java
getServer().getPermissionManager().registerPermission(
        new Permission("hello.use", "Use /hello", PermissionDefault.ALLOW, this));

if (sender.hasPermission("hello.use")) {
    sender.sendMessage("Allowed.");
}
```

A permission manager plugin can call `addAttachment(plugin)` on any `Permissible` to allow or deny
nodes for one player. The most specific rule wins: an exact node beats a `parent.*` wildcard, and an
explicit denial beats registered defaults and operator status.

## Building and testing

```bash
bash mvnw clean install
bash tests/run.sh
```

The API has no runtime dependencies. `tests/run.sh` uses plain Java assertions, so no test
framework is needed.

## License

GPL-3.0, the same as Netherrack itself. See `LICENSE`.
