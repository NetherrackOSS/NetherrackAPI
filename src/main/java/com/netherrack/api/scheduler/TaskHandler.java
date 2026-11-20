package com.netherrack.api.scheduler;

import com.netherrack.api.plugin.Plugin;

/**
 * A scheduled task, which can be cancelled by id or through this handle.
 * <p>
 * Tasks run on the server's tick thread, so a handler may touch players and the world that same
 * tick. Work that would block, such as disk or network I/O, belongs on the plugin's own thread.
 */
public interface TaskHandler {

    int getTaskId();

    Plugin getOwner();

    boolean isCancelled();

    void cancel();
}
