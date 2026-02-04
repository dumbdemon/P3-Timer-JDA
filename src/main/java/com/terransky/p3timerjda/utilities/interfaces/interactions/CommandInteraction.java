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
    private final List<Long> restrictedServers = new ArrayList<>();
    private final List<Permission> defaultMemberPermissions = new ArrayList<>();
    private final InteractionType interactionType;
    private boolean isWorking = true;
    private boolean isOwnerOnly = false;
    private boolean isDeveloperOnly = false;
    private String disabledReason = "";

    protected CommandInteraction(String name, InteractionType interactionType) {
        this.name = name;
        this.interactionType = interactionType;
    }

    public boolean isWorking() {
        return isWorking;
    }

    protected void setWorking(boolean working) {
        isWorking = working;
    }

    public boolean isDisabled() {
        return !disabledReason.isEmpty();
    }

    public boolean isGuildPrivate() {
        return !getRestrictedServers().isEmpty();
    }

    public boolean isOwnerOnly() {
        return isOwnerOnly;
    }

    protected void setOwnerOnly() {
        isOwnerOnly = true;
    }

    public boolean isDeveloperOnly() {
        return isDeveloperOnly;
    }

    protected void setDeveloperOnly(boolean developerOnly) {
        isDeveloperOnly = developerOnly;
    }

    protected void setDeveloperOnly() {
        isDeveloperOnly = true;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    protected void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    public CommandData getCommandData() {
        return null;
    }

    public List<Long> getRestrictedServers() {
        return List.copyOf(restrictedServers);
    }

    protected void addRestrictedServer(List<Long> restrictedServers) {
        this.restrictedServers.addAll(restrictedServers);
    }

    protected void addRestrictedServer(long serverId) {
        addRestrictedServer(List.of(serverId));
    }

    public List<Permission> getDefaultMemberPermissions() {
        return defaultMemberPermissions;
    }

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
