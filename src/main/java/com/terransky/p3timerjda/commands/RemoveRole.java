package com.terransky.p3timerjda.commands;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.exceptions.DiscordAPIException;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.SlashCommandInteraction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class RemoveRole extends SlashCommandInteraction {

    public RemoveRole() {
        super("remove", "Remove a role from being watched.");
        addOptions(
            new OptionData(OptionType.ROLE, "role", "The role to remove", true)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException {
        Optional<Role> optionalRole = Optional.ofNullable(event.getOption("role", OptionMapping::getAsRole));
        if (optionalRole.isEmpty()) throw new DiscordAPIException();
        Role toRemoveRole = optionalRole.get();

        if (!P3TimerJDA.getRolesConfig().get().removeRole(blob.getGuildIdLong(), toRemoveRole.getIdLong())) {
            actionFailed(event);
            return;
        }

        event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
            String.format("%s has been removed. It will no longer be watched.", toRemoveRole.getAsMention())
        )).queue();
    }
}
