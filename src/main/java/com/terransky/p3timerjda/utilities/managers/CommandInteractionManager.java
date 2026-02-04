package com.terransky.p3timerjda.utilities.managers;

import com.terransky.p3timerjda.utilities.general.InteractionType;
import com.terransky.p3timerjda.utilities.interfaces.interactions.CommandInteraction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommandInteractionManager<T extends CommandInteraction<?>> extends InteractionManager<T> {

    public CommandInteractionManager(InteractionType interactionType) {
        super(interactionType);
    }

    private int getLimit(@NotNull List<T> toDeployInteractions) {
        InteractionType interactionType = toDeployInteractions.get(0).getInteractionType();
        if (toDeployInteractions.size() > interactionType.getMaximum())
            LoggerFactory.getLogger(
                getClass()).warn("You can only have {} {}s. Limiting interactions...", interactionType.getMaximum(), interactionType.getName()
            );
        return interactionType.getMaximum();
    }

    public List<CommandData> getCommandData() {
        List<T> toDeployInteractions = interactions.stream()
            .filter(CommandInteraction::isWorking)
            .filter(interaction -> !interaction.isGuildPrivate())
            .toList();
        int limit;

        if (!toDeployInteractions.isEmpty()) {
            limit = getLimit(toDeployInteractions);
        } else return new ArrayList<>();

        return toDeployInteractions.stream()
            .limit(limit)
            .map(CommandInteraction::getCommandData)
            .toList();
    }

    public List<CommandData> getCommandData(Guild guild) {
        List<T> toDeployInteractions = interactions.stream()
            .filter(CommandInteraction::isWorking)
            .filter(CommandInteraction::isGuildPrivate)
            .filter(interaction -> interaction.getRestrictedServers().contains(guild.getIdLong()))
            .toList();
        int limit;

        if (!toDeployInteractions.isEmpty()) {
            limit = getLimit(toDeployInteractions);
        } else return new ArrayList<>();

        return toDeployInteractions.stream()
            .limit(limit)
            .map(CommandInteraction::getCommandData)
            .toList();
    }
}
