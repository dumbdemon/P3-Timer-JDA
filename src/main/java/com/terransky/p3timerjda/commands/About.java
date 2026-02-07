package com.terransky.p3timerjda.commands;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.SlashCommandInteraction;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class About extends SlashCommandInteraction {
    public About() {
        super("about", "Information about the bot.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException {
        event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, P3TimerJDA.getConfig().getDescription())).queue();
    }
}
