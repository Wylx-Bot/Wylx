package com.wylxbot.wylx.Core.Util;

import com.wylxbot.wylx.Database.DbElements.DiscordGlobal;
import com.wylxbot.wylx.Database.DbElements.GlobalIdentifiers;
import com.wylxbot.wylx.Wylx;

import java.util.Timer;
import java.util.TimerTask;

public class WylxStats {
    private long commandsProcessed;
    private long averageCommandTime;

    private long silentEventsProcessed;
    private long averageSilentEventTime;

    private long noOpsProcessed;
    private long averageNoOpTime;

    private final Timer writeTimer = new Timer("WylxStatsWriteTimer");

    public WylxStats(long commandsProcessed, long averageCommandTime,
                     long silentEventsProcessed, long averageSilentEventTime,
                     long noOpsProcessed, long averageNoOpTime) {
        this.commandsProcessed = commandsProcessed;
        this.averageCommandTime = averageCommandTime;
        this.silentEventsProcessed = silentEventsProcessed;
        this.averageSilentEventTime = averageSilentEventTime;
        this.noOpsProcessed = noOpsProcessed;
        this.averageNoOpTime = averageNoOpTime;

        // Schedule updating of the db every 10 minutes
        writeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DiscordGlobal globalDB = Wylx.getInstance().getDb().getGlobal();

                WylxStats stats = globalDB.getSetting(GlobalIdentifiers.BotStats);

                globalDB.setSetting(GlobalIdentifiers.BotStats, stats);
            }
        }, 0, 600000);
    }

    public void updateAverageCommand(long startTime) {
        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Calc new avg
        averageCommandTime = ((averageCommandTime * commandsProcessed) / (++commandsProcessed)) + (runTime / (commandsProcessed));
    }

    public void updateAverageNoOp(long startTime) {
        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Calc new avg
        averageNoOpTime = ((averageNoOpTime * noOpsProcessed) / (++noOpsProcessed)) + (runTime / (noOpsProcessed));
    }

    public void updateAverageSilentEvent(long startTime) {
        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Calc new avg
        averageSilentEventTime = ((averageSilentEventTime * silentEventsProcessed) / (++silentEventsProcessed)) + (runTime / (silentEventsProcessed));
    }

    public long getCommandsProcessed() {
        return commandsProcessed;
    }

    public long getAverageCommandTime() {
        return averageCommandTime;
    }

    public long getSilentEventsProcessed() {
        return silentEventsProcessed;
    }

    public long getAverageSilentEventTime() {
        return averageSilentEventTime;
    }

    public long getNoOpsProcessed() {
        return noOpsProcessed;
    }

    public long getAverageNoOpTime() {
        return averageNoOpTime;
    }
}
