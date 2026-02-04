package com.terransky.p3timerjda.utilities.interfaces.interactions;

import com.terransky.p3timerjda.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class MessageCommandInteraction extends CommandInteraction<MessageContextInteractionEvent> {

    protected MessageCommandInteraction(String name) {
        super(name, InteractionType.COMMAND_MESSAGE);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.message(getName())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getDefaultMemberPermissions()));
    }
}
