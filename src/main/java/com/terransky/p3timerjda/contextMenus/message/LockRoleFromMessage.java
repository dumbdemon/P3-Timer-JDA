package com.terransky.p3timerjda.contextMenus.message;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.modals.WatchRole;
import com.terransky.p3timerjda.utilities.command.BotColors;
import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.MessageCommandInteraction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LockRoleFromMessage extends MessageCommandInteraction {

    public LockRoleFromMessage() {
        super("Lock Role In Message");
    }

    @Override
    public void execute(@NotNull MessageContextInteractionEvent event, EventBlob blob) throws FailedInteractionException {
        List<Role> roles = event.getTarget().getMentions().getRoles();

        if (roles.isEmpty()) {
            event.replyComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME, "## No Role in message.", BotColors.SUB_DEFAULT)).setEphemeral(true).queue();
            return;
        }

        event.replyModal(new WatchRole().getContructedModal(roles)).queue();
    }
}
