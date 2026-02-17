package com.terransky.p3timerjda.commands;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.*;
import com.terransky.p3timerjda.utilities.exceptions.DiscordAPIException;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.SlashCommandInteraction;
import com.terransky.p3timerjda.utilities.interfaces.roles.RoleConfig;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AddRole extends SlashCommandInteraction {

    public static final List<OptionData> ROLE_OPTIONS = List.of(
        new OptionData(OptionType.ROLE, "role", "The role to watch", true),
        new OptionData(OptionType.INTEGER, "timeout", "how long to timeout the role when mentioned.", false)
            .setMinValue(1),
        new OptionData(OptionType.STRING, "interval", "What time frame to use?", false)
            .addChoices(
                new Command.Choice("Seconds", "sec"),
                new Command.Choice("Minutes", "min"),
                new Command.Choice("Hours", "hr")
            )
    );

    public AddRole() {
        super("add", "Adds a role to watch.");
        addOptions(
            ROLE_OPTIONS
        );
    }

    @Nullable
    public static WatchedRole getWatchedRole(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws DiscordAPIException {
        Optional<Role> optionalRole = Optional.ofNullable(event.getOption("role", OptionMapping::getAsRole));
        int baseTimeout = event.getOption("timeout", 1, OptionMapping::getAsInt);
        String interval = event.getOption("interval", "hr", OptionMapping::getAsString);

        if (optionalRole.isEmpty()) throw new DiscordAPIException();
        Role watchedRole = optionalRole.get();

        return getWatchedRole(event, blob, interval, baseTimeout, watchedRole);
    }

    @Nullable
    public static <T extends GenericInteractionCreateEvent> WatchedRole getWatchedRole(@NotNull T event, EventBlob blob, @NotNull String interval, int baseTimeout, Role watchedRole) {
        long timeout;
        TimeUnit timeUnit;
        switch (interval) {
            case "sec" -> {
                timeout = baseTimeout;
                timeUnit = TimeUnit.SECONDS;
            }
            case "min" -> {
                timeout = TimeUnit.MINUTES.toSeconds(baseTimeout);
                timeUnit = TimeUnit.MINUTES;
            }
            default -> {
                timeout = TimeUnit.HOURS.toSeconds(baseTimeout);
                timeUnit = TimeUnit.HOURS;
            }
        }

        if (blob.getGuild().getBotRole() == null) {
            String message = "No Bot role is present. Did you configure the invite correctly?";

            if (event instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
                slashCommandInteractionEvent.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, message, BotColors.ERROR)
                ).queue();
            } else if (event instanceof ModalInteractionEvent modalInteractionEvent) {
                modalInteractionEvent.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, message, BotColors.ERROR)
                ).queue();
            }
            return null;
        }
        if (!blob.getGuild().getBotRole().canInteract(watchedRole)) {
            String message = String.format("Unable to interact with %s. Please put my role [%s] higher than all of the roles to be watched.",
                watchedRole.getAsMention(),
                blob.getGuild().getBotRole().getAsMention()
            );

            if (event instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
                slashCommandInteractionEvent.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, message, BotColors.ERROR)).queue();
            } else if (event instanceof ModalInteractionEvent modalInteractionEvent) {
                modalInteractionEvent.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, message, BotColors.ERROR)).queue();
            }
            return null;
        }

        if (!watchedRole.isMentionable() && !P3TimerJDA.getRolesConfig().get().isWatching(blob.getGuild(), watchedRole)) {
            String message = String.format("%s provided is not mentionable. Please enable it in settings or choose a different role.)", watchedRole.getAsMention());

            if (event instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
                slashCommandInteractionEvent.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, message, BotColors.ERROR)).queue();
            } else if (event instanceof ModalInteractionEvent modalInteractionEvent) {
                modalInteractionEvent.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, message, BotColors.ERROR)).queue();
            }
            return null;
        }

        return new WatchedRole(baseTimeout, timeUnit, timeout, watchedRole);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException {
        WatchedRole roleWatch = getWatchedRole(event, blob);
        if (roleWatch == null) return;

        if (P3TimerJDA.getRolesConfig().get().addRole(blob.getGuildIdLong(), roleWatch.watchedRole().getIdLong(), roleWatch.timeout())) {
            RoleConfig roleConfig = P3TimerJDA.getRolesConfig().get().getRole(blob.getGuildIdLong(), roleWatch.watchedRole().getIdLong()).orElseThrow(IOException::new);
            TimeData timeData = TimeData.getTimeData(roleConfig.getTimeout());
            event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
                String.format("%s has already been added with a timeout of %s %s%s.",
                    roleWatch.watchedRole().getAsMention(),
                    timeData.timeout(),
                    timeData.getLengthType(),
                    timeData.timeout() > 1 ? "s" : "")
            )).setEphemeral(true).queue();
            return;
        }

        event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
            String.format("%s added with a timeout of %s %s%s.",
                roleWatch.watchedRole().getAsMention(),
                roleWatch.baseTimeout(),
                roleWatch.getLengthType(),
                roleWatch.baseTimeout() > 1 ? "s" : ""
            )
        )).queue();
    }
}
