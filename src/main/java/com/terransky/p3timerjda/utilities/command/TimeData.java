package com.terransky.p3timerjda.utilities.command;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public record TimeData(TimeUnit interval, long timeout) {
    @NotNull
    @Contract("_ -> new")
    public static TimeData getTimeData(long timeout) {
        if (TimeUnit.SECONDS.toHours(timeout) >= 1L)
            return new TimeData(TimeUnit.HOURS, TimeUnit.SECONDS.toHours(timeout));
        if (TimeUnit.SECONDS.toMinutes(timeout) >= 1L)
            return new TimeData(TimeUnit.MINUTES, TimeUnit.SECONDS.toMinutes(timeout));
        return new TimeData(TimeUnit.SECONDS, timeout);
    }

    @NotNull
    @Contract(pure = true)
    public String getLengthType() {
        switch (interval) {
            case SECONDS -> {
                return "sec";
            }
            case MINUTES -> {
                return "min";
            }
            default -> {
                return "hr";
            }
        }
    }
}
