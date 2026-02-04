package com.terransky.p3timerjda.commands;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.exceptions.DiscordAPIException;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.SlashCommandInteraction;
import com.terransky.p3timerjda.utilities.interfaces.roles.RolesDatum;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SetReportingChannel extends SlashCommandInteraction {

    public SetReportingChannel() {
        super("reporting-channel", "Reporting channel config.");
        addSubcommands(
            new SubcommandData("enable", "Set a channel to send reports. Defaults to the channel where its been called.")
                .addOptions(
                    new OptionData(OptionType.CHANNEL, "channel", "The channel to send reports to.")
                ),
            new SubcommandData("disable", "Remove channel remorting")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null || subcommand.isBlank())
            throw new DiscordAPIException();

        else if (subcommand.equals("enable")) {
            GuildChannelUnion reportingChannel = (GuildChannelUnion) event.getOption("channel", event.getChannel(), OptionMapping::getAsChannel);

            if (!blob.getSelfMember().hasPermission(reportingChannel)) {
                event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
                    String.format("I do not have access to that channel. Please grant access to %s to proceed.", reportingChannel.getAsMention())
                )).queue();
                return;
            }

            setChannelAndSendMessage(event, blob, reportingChannel.getIdLong(), String.format("%s has been set as the reporting channel.", reportingChannel.getAsMention()));
        } else
            setChannelAndSendMessage(event, blob, RolesDatum.getDisabledChannel(), "Reporting has been disabled.");
    }

    private void setChannelAndSendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, long channelID, String message) {
        if (!P3TimerJDA.getRolesConfig().get().setReportingChannel(blob.getGuildIdLong(), channelID)) {
            actionFailed(event);
            return;
        }

        event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, message)).queue();
    }
}
