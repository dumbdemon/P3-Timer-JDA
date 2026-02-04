package com.terransky.p3timerjda.utilities.interfaces.interactions;

import com.terransky.p3timerjda.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class UserCommandInteraction extends CommandInteraction<UserContextInteractionEvent> {

    protected UserCommandInteraction(String name) {
        super(name, InteractionType.COMMAND_USER);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.user(getName())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getDefaultMemberPermissions()));
    }
}
