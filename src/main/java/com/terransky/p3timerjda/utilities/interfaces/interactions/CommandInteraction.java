package com.terransky.p3timerjda.utilities.interfaces.interactions;

import com.terransky.p3timerjda.utilities.general.InteractionType;
import com.terransky.p3timerjda.utilities.interfaces.IInteraction;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandInteraction<T extends GenericCommandInteractionEvent> implements IInteraction.ICommand<T> {

    private final String name;
    private final List<Permission> defaultMemberPermissions = new ArrayList<>();
    private final InteractionType interactionType;
    private boolean isWorking = true;

    protected CommandInteraction(String name, InteractionType interactionType) {
        this.name = name;
        this.interactionType = interactionType;
    }

    public boolean isWorking() {
        return isWorking;
    }

    @SuppressWarnings("unused")
    protected void setWorking(boolean working) {
        isWorking = working;
    }

    public CommandData getCommandData() {
        return null;
    }

    public List<Permission> getDefaultMemberPermissions() {
        return defaultMemberPermissions;
    }

    @SuppressWarnings("unused")
    protected void setDefaultMemberPermissions(Permission... defaultMemberPermissions) {
        this.defaultMemberPermissions.addAll(Arrays.asList(defaultMemberPermissions));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InteractionType getInteractionType() {
        return interactionType;
    }
}
