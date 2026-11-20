# NetherrackAPI
[![](https://jitpack.io/v/NetherrackOSS/NetherrackAPI.svg)](https://jitpack.io/#NetherrackOSS/NetherrackAPI)

Plugin API for [Netherrack](https://github.com/NetherrackOSS/Netherrack), an experimental
Minecraft: Bedrock Edition server written in Java 17.

This repository contains the plugin contracts: lifecycle callbacks, listeners and events, and
small `Server`, `Player`, `World` and `BlockSnapshot` interfaces. The scheduler, commands and
permissions are still planned; see `PLUGIN_API_PLAN.md` in the server repository for the design.

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
player's network thread. A handler should return quickly; schedulers for slower work are planned.

## Building and testing

```bash
bash mvnw clean install
bash tests/run.sh
```

The API has no runtime dependencies. `tests/run.sh` uses plain Java assertions, so no test
framework is needed.

## License

GPL-3.0, the same as Netherrack itself. See `LICENSE`.
