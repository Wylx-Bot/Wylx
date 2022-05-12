package com.wylxbot.wylx.Core.Util;

import com.wylxbot.wylx.Database.DbElements.DiscordGlobal;
import com.wylxbot.wylx.Database.DbElements.GlobalIdentifiers;
import com.wylxbot.wylx.Wylx;

import java.util.Timer;
import java.util.TimerTask;

public class WylxStats {
    private long commandsProcessed;
    private double averageCommandTime;

    private long silentEventsProcessed;
    private double averageSilentEventTime;

    private long noOpsProcessed;
    private double averageNoOpTime;

    private final Timer writeTimer = new Timer("WylxStatsWriteTimer");

    private static final long TEN_MINUTES_IN_MS = 600000;

    public WylxStats(long commandsProcessed, double averageCommandTime,
                     long silentEventsProcessed, double averageSilentEventTime,
                     long noOpsProcessed, double averageNoOpTime) {
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
        }, 0, TEN_MINUTES_IN_MS);
    }

    public void updateAverageCommand(long startTime) {
        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Calc new avg
        averageCommandTime = runningAverageCalculation(averageCommandTime, commandsProcessed++, runTime);
    }

    public void updateAverageNoOp(long startTime) {
        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Calc new avg
        averageNoOpTime = runningAverageCalculation(averageNoOpTime, noOpsProcessed++, runTime);
    }

    public void updateAverageSilentEvent(long startTime) {
        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Calc new avg
        averageSilentEventTime = runningAverageCalculation(averageSilentEventTime, silentEventsProcessed++, runTime);
    }

    private double runningAverageCalculation(double oldAvg, long oldCount, long newDataPoint) {
        double adjustedOldAvg = oldAvg * oldCount;
        double newAvg = (adjustedOldAvg + newDataPoint) / (oldCount + 1);
        return newAvg;
    }

    public long getCommandsProcessed() {
        return commandsProcessed;
    }

    public double getAverageCommandTime() {
        return averageCommandTime;
    }

    public long getSilentEventsProcessed() {
        return silentEventsProcessed;
    }

    public double getAverageSilentEventTime() {
        return averageSilentEventTime;
    }

    public long getNoOpsProcessed() {
        return noOpsProcessed;
    }

    public double getAverageNoOpTime() {
        return averageNoOpTime;
    }
}
