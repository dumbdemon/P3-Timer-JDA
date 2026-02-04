package com.terransky.p3timerjda;


import com.terransky.p3timerjda.commands.AddRole;
import com.terransky.p3timerjda.commands.RemoveRole;
import com.terransky.p3timerjda.commands.SetReportingChannel;
import com.terransky.p3timerjda.commands.UpdateRole;
import com.terransky.p3timerjda.utilities.command.SlashCommandMetadata;
import com.terransky.p3timerjda.utilities.general.InteractionType;
import com.terransky.p3timerjda.utilities.interfaces.interactions.*;
import com.terransky.p3timerjda.utilities.managers.ButtonInteractionManager;
import com.terransky.p3timerjda.utilities.managers.CommandInteractionManager;
import com.terransky.p3timerjda.utilities.managers.InteractionManager;

import java.util.List;

public class Managers {

    Managers() {
    }

    public static class SlashCommands extends CommandInteractionManager<SlashCommandInteraction> {

        public SlashCommands() {
            super(InteractionType.COMMAND_SLASH);
            addInteraction(new AddRole());
            addInteraction(new RemoveRole());
            addInteraction(new SetReportingChannel());
            addInteraction(new UpdateRole());
        }

        public List<SlashCommandMetadata> getCommandMetadata() {
            return interactions.stream()
                .map(SlashCommandMetadata::new)
                .sorted()
                .toList();
        }
    }

    public static class DiscordButtons extends ButtonInteractionManager {

        public DiscordButtons() {
        }
    }

    public static class MessageContextMenu extends CommandInteractionManager<MessageCommandInteraction> {

        public MessageContextMenu() {
            super(InteractionType.COMMAND_MESSAGE);
        }
    }

    public static class UserContextMenu extends CommandInteractionManager<UserCommandInteraction> {

        public UserContextMenu() {
            super(InteractionType.COMMAND_USER);
        }
    }

    public static class EntitySelectMenu extends InteractionManager<SelectMenuEntityInteraction> {

        public EntitySelectMenu() {
            super(InteractionType.SELECTION_ENTITY);
        }
    }

    public static class StringSelectMenu extends InteractionManager<SelectMenuStringInteraction> {

        public StringSelectMenu() {
            super(InteractionType.SELECTION_STRING);
        }
    }

    public static class ModalInteractions extends InteractionManager<ModalInteraction> {

        public ModalInteractions() {
            super(InteractionType.MODAL);
        }
    }
}
