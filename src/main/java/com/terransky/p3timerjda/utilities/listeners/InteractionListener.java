package com.terransky.p3timerjda.utilities.listeners;

import com.terransky.p3timerjda.Managers;
import com.terransky.p3timerjda.utilities.command.BotColors;
import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.general.InteractionType;
import com.terransky.p3timerjda.utilities.interfaces.IInteraction;
import com.terransky.p3timerjda.utilities.interfaces.interactions.*;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class InteractionListener extends ListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(InteractionListener.class);

    @NotNull
    private Container getFailedInteractionMessage(@NotNull EventBlob blob) {
        return StandardResponse.getResponseContainer("Interaction Failure",
            String.format("%s failed. Please contact the developer.", blob.getInteractionType().getName()),
            BotColors.ERROR);
    }

    private void logInteractionFailure(String interactionName, String guildID, Exception e) {
        String message = String.format("%S failed to execute on guild id %s", interactionName, guildID);
        log.error(message, e);
    }

    private void errorHandler(@NotNull GenericCommandInteractionEvent event, @NotNull IInteraction<?> interaction, EventBlob blob, Exception e) {
        Container commandFailed = getFailedInteractionMessage(blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged())
            event.getHook().sendMessageComponents(commandFailed).queue();
        else event.replyComponents(commandFailed).setEphemeral(true).queue();
    }

    private void errorHandler(@NotNull GenericComponentInteractionCreateEvent event, @NotNull IInteraction<?> interaction, EventBlob blob, Exception e) {
        Container commandFailed = getFailedInteractionMessage(blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged())
            event.getHook().sendMessageComponents(commandFailed).queue();
        else event.replyComponents(commandFailed).setEphemeral(true).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            return;
        }

        Optional<SlashCommandInteraction> ifSlash = (new Managers.SlashCommands()).getInteraction(event.getName());
        if (ifSlash.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_SLASH)
            .setChannelUnion(event.getChannel());

        SlashCommandInteraction slash = ifSlash.get();

        try {
            slash.logInteraction(log);
            slash.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            log.debug("Full command path that triggered error :: [{}]", event.getFullCommandName());
            errorHandler(event, slash, blob, e);
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        Optional<MessageCommandInteraction> ifMenu = (new Managers.MessageContextMenu()).getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_MESSAGE)
            .setChannelUnion(event.getChannel());

        MessageCommandInteraction commandMessage = ifMenu.get();

        try {
            commandMessage.logInteraction(log);
            commandMessage.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, commandMessage, blob, e);
        }
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        Optional<UserCommandInteraction> ifMenu = (new Managers.UserContextMenu()).getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_USER);

        UserCommandInteraction commandUser = ifMenu.get();

        try {
            commandUser.logInteraction(log);
            commandUser.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, commandUser, blob, e);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        Optional<ButtonInteraction> ifButton = (new Managers.DiscordButtons()).getInteraction(event.getComponentId());
        if (ifButton.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.BUTTON)
            .setChannelUnion(event.getChannel());

        ButtonInteraction iButton = ifButton.get();
        try {
            iButton.logInteraction(log, String.format("%s<%s>", iButton.getNameReadable(), event.getComponentId()));
            iButton.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, iButton, blob, e);
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        Optional<SelectMenuEntityInteraction> ifMenu = (new Managers.EntitySelectMenu()).getInteraction(event.getInteraction().getComponentId());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.SELECTION_ENTITY)
            .setChannelUnion(event.getChannel());
        SelectMenuEntityInteraction menu = ifMenu.get();
        try {
            menu.logInteraction(log);
            menu.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, menu, blob, e);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        Optional<ModalInteraction> ifModal = (new Managers.ModalInteractions()).getInteraction(event.getModalId());
        if (ifModal.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.MODAL)
            .setChannelUnion(event.getChannel());
        ModalInteraction modal = ifModal.get();
        try {
            modal.logInteraction(log);
            modal.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            logInteractionFailure(modal.getName(), blob.getGuildId(), e);
            if (event.isAcknowledged())
                event.getHook().sendMessageComponents(getFailedInteractionMessage(blob)).queue();
            else event.replyComponents(getFailedInteractionMessage(blob)).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        List<SelectMenuStringInteraction> menus = new ArrayList<>() {{
            var selectMenuManager = new Managers.StringSelectMenu();
            for (String menuName : event.getInteraction().getValues()) {
                selectMenuManager.getInteraction(menuName).ifPresent(this::add);
            }
        }};

        String componentId = event.getComponentId();
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.SELECTION_STRING)
            .setChannelUnion(event.getChannel());

        for (SelectMenuStringInteraction stringMenu : menus) {
            try {
                stringMenu.logInteraction(log, String.format("%s<%s>", componentId.toUpperCase(), stringMenu.getNameReadable()));
                stringMenu.execute(event, blob);
            } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
                errorHandler(event, stringMenu, blob, e);
            }
        }
    }
}
