package com.terransky.p3timerjda.commands;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.BotColors;
import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.exceptions.DiscordAPIException;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.SlashCommandInteraction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AddRole extends SlashCommandInteraction {

    public static OptionData[] ROLE_OPTIONS = {
        new OptionData(OptionType.ROLE, "role", "The role to watch", true),
        new OptionData(OptionType.INTEGER, "timeout", "how long to timeout the role when mentioned.", false),
        new OptionData(OptionType.STRING, "interval", "What time frame to use?", false)
            .addChoices(
            new Command.Choice("Seconds", "sec"),
            new Command.Choice("Minutes", "min"),
            new Command.Choice("Hours", "hr")
        )
    };

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
        long timeout;

        if (optionalRole.isEmpty()) throw new DiscordAPIException();
        Role watchedRole = optionalRole.get();

        switch (interval) {
            case "sec" -> timeout = baseTimeout;
            case "min" -> timeout = TimeUnit.MINUTES.toSeconds(baseTimeout);
            default -> timeout = TimeUnit.HOURS.toSeconds(baseTimeout);
        }

        if (!Objects.requireNonNull(blob.getGuild().getBotRole()).canInteract(watchedRole)) {
            event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
                String.format("Unable to interact with %s. Please put my role [%s] higher than all of the roles to be watched.",
                    watchedRole.getAsMention(),
                    blob.getGuild().getBotRole().getAsMention()
                ), BotColors.ERROR)
            ).queue();
            return null;
        }
        return new WatchedRole(baseTimeout, interval, timeout, watchedRole);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException {
        WatchedRole roleWatch = getWatchedRole(event, blob);
        if (roleWatch == null) return;

        if (!P3TimerJDA.getRolesConfig().get().addRole(blob.getGuildIdLong(), roleWatch.watchedRole().getIdLong(), roleWatch.timeout())) {
            actionFailed(event);
            return;
        }

        event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
            String.format("%s added with a timeout of %s %s%s.",
                roleWatch.watchedRole().getAsMention(),
                roleWatch.baseTimeout(),
                roleWatch.interval(),
                roleWatch.baseTimeout() > 1 ? "s" : ""
            )
        )).queue();
    }

    public record WatchedRole(int baseTimeout, String interval, long timeout, Role watchedRole) {
    }
}
