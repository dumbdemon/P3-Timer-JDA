package com.terransky.p3timerjda.utilities.general;

import net.dv8tion.jda.api.interactions.commands.build.Commands;

public enum InteractionType {

    UNKNOWN("UNKNOWN", 0), //For future interactions
    COMMAND_SLASH("Slash Command", Commands.MAX_SLASH_COMMANDS, true),
    COMMAND_USER("User Context Menu", Commands.MAX_USER_COMMANDS, true),
    COMMAND_MESSAGE("Message Context Menu", Commands.MAX_MESSAGE_COMMANDS, true),
    BUTTON("Button", true),
    MODAL("Modal"),
    SELECTION_STRING("Selection Menu"),
    SELECTION_ENTITY("Entity Selection Menu");

    private final String name;
    private final int maximum;
    private final boolean hasDedicatedManager;

    InteractionType(String name) {
        this(name, Integer.MAX_VALUE);
    }

    InteractionType(String name, boolean hasDedicatedManager) {
        this(name, Integer.MAX_VALUE, hasDedicatedManager);
    }

    InteractionType(String name, int maximum) {
        this(name, maximum, false);
    }

    InteractionType(String name, int maximum, boolean hasDedicatedManager) {
        this.name = name;
        this.maximum = maximum;
        this.hasDedicatedManager = hasDedicatedManager;
    }

    public String getName() {
        return name;
    }

    public int getMaximum() {
        return maximum;
    }

    public boolean hasDedicatedManager() {
        return hasDedicatedManager;
    }
}
