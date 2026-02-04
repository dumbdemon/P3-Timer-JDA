package com.terransky.p3timerjda.commands;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.SlashCommandInteraction;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UpdateRole extends SlashCommandInteraction {

    public UpdateRole() {
        super("update", "Changes the role settings.");
        addOptions(AddRole.ROLE_OPTIONS);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException {
        AddRole.WatchedRole watchedRole = AddRole.getWatchedRole(event, blob);
        if (watchedRole == null) return;

        if (!P3TimerJDA.getRolesConfig().get().updateRole(blob.getGuildIdLong(), watchedRole.watchedRole().getIdLong(), watchedRole.timeout())) {
            actionFailed(event);
            return;
        }

        event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
            String.format("%s updated with a timeout of %s %s%s.",
                watchedRole.watchedRole().getAsMention(),
                watchedRole.baseTimeout(),
                watchedRole.interval(),
                watchedRole.baseTimeout() > 1 ? "s" : ""

            )
        )).queue();
    }
}
