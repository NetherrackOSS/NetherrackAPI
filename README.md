# NetherrackAPI

Plugin API for [Netherrack](https://github.com/NetherrackOSS/Netherrack), an experimental
Minecraft: Bedrock Edition server written in Java 17.

This repository currently contains the **lifecycle layer**: enough for a plugin to be loaded,
enabled and disabled by the server. Events, scheduler, commands and permissions are planned;
see `PLUGIN_API_PLAN.md` in the server repository for the full design.

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

import com.netherrack.api.plugin.PluginBase;

public final class HelloPlugin extends PluginBase {

    @Override
    public void onLoad() {
        getLogger().info("HelloPlugin loaded");
    }

    @Override
    public void onEnable() {
        getLogger().info("HelloPlugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("HelloPlugin disabled");
    }
}
```

`onLoad()` runs while the server is still starting, before the world and network exist. Use it
only for setup that doesn't touch them. `onEnable()` runs once the server is ready, and
`onDisable()` on shutdown in reverse load order.

## Building and testing

```bash
mvn clean install
bash tests/run.sh
```

The API has no runtime dependencies. `tests/run.sh` uses plain Java assertions, so no test
framework is needed.

## License

GPL-3.0, the same as Netherrack itself. See `LICENSE`.
