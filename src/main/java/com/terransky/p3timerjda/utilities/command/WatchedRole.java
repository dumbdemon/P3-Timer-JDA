package com.terransky.p3timerjda.utilities.command;

import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public record WatchedRole(int baseTimeout, TimeUnit interval, long timeout, Role watchedRole) {

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
