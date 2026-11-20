package com.netherrack.api.scheduler;

import com.netherrack.api.plugin.Plugin;

/**
 * Runs plugin work on the server tick, twenty ticks to the second.
 * <p>
 * Every task records its owning plugin. Disabling that plugin cancels all of its tasks, so a timer
 * cannot keep firing after its jar has been closed. A delay of zero means the next tick, never the
 * current one.
 */
public interface Scheduler {

    TaskHandler runTask(Plugin plugin, Runnable task);

    TaskHandler runTaskLater(Plugin plugin, Runnable task, long delayTicks);

    TaskHandler runTaskTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks);

    /** Cancels one task by id, doing nothing if it has already finished or been cancelled. */
    void cancelTask(int taskId);

    /** Cancels every task owned by this plugin. */
    void cancelTasks(Plugin plugin);
}
