package com.terransky.p3timerjda.utilities.interfaces.interactions;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SlashCommandInteraction extends CommandInteraction<SlashCommandInteractionEvent> {

    private final String description;
    private final List<SubcommandGroupData> subcommandGroups = new ArrayList<>();
    private final List<SubcommandData> subcommands = new ArrayList<>();
    private final List<OptionData> options = new ArrayList<>();
    private boolean isNSFW = false;

    protected SlashCommandInteraction(String name, String description) {
        super(name, InteractionType.COMMAND_SLASH);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unused")
    protected void addSubcommandGroups(SubcommandGroupData... subcommandGroup) {
        subcommandGroups.addAll(Arrays.asList(subcommandGroup));
    }

    protected void addSubcommands(SubcommandData... subcommand) {
        subcommands.addAll(Arrays.asList(subcommand));
    }

    protected void addOptions(OptionData... option) {
        addOptions(Arrays.asList(option));
    }

    protected void addOptions(List<OptionData> options) {
        this.options.addAll(options);
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    @SuppressWarnings("unused")
    protected void setNSFW() {
        isNSFW = true;
    }

    protected void actionFailed(@NotNull SlashCommandInteractionEvent event) {
        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
            "Unable to complete command. Please try again. If it still does not work, please let the developer know."
        )).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription())
            .setIntegrationTypes(IntegrationType.GUILD_INSTALL)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getDefaultMemberPermissions()))
            .setNSFW(isNSFW());

        if (!options.isEmpty()) {
            return commandData.addOptions(options);
        }

        if (!subcommands.isEmpty() || !subcommandGroups.isEmpty()) {
            return commandData.addSubcommands(subcommands)
                .addSubcommandGroups(subcommandGroups);
        }

        return commandData;
    }
}
