package com.terransky.p3timerjda.utilities.interfaces;

import com.terransky.p3timerjda.utilities.command.EventBlob;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface IInteraction<T extends GenericInteractionCreateEvent> extends Comparable<IInteraction<T>> {

    int MAX_MESSAGE_LENGTH = 4000 - 32;

    /**
     * The name or ID reference of this bot element.
     *
     * @return A {@link String} of this bot element.
     */
    String getName();

    default String getNameReadable() {
        return WordUtils.capitalize(this.getName().replaceAll("-", " "));
    }

    /**
     * Get the interaction type.
     *
     * @return An {@link InteractionType}.
     */
    default InteractionType getInteractionType() {
        return InteractionType.UNKNOWN;
    }


    /**
     * The main handler for all interactions
     *
     * @param event A Discord {@link GenericInteractionCreateEvent event}
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws FailedInteractionException Any exception thrown that could prevent operation.
     * @throws IOException                Potentially could be thrown during network operations
     * @throws ExecutionException         Thrown when a {@link java.util.concurrent.CompletableFuture CompletableFuture}.
     *                                    {@link java.util.concurrent.CompletableFuture#get() get()} could not be completed
     * @throws InterruptedException       Thrown when a {@link java.util.concurrent.CompletableFuture CompletableFuture}.
     *                                    {@link java.util.concurrent.CompletableFuture#get() get()} call gets interrupted.
     */
    void execute(@NotNull T event, EventBlob blob)
        throws FailedInteractionException, IOException, ExecutionException, InterruptedException;

    @Override
    default int compareTo(@NotNull IInteraction iInteraction) {
        return String.CASE_INSENSITIVE_ORDER.compare(getName(), iInteraction.getName()) |
            getInteractionType().compareTo(iInteraction.getInteractionType());
    }

    interface ICommand<T extends GenericCommandInteractionEvent> extends IInteraction<T> {
    }

    interface IButton extends IInteraction<ButtonInteractionEvent> {
        @Override
        default InteractionType getInteractionType() {
            return InteractionType.BUTTON;
        }
    }

    interface IModal extends IInteraction<ModalInteractionEvent> {
        @Override
        default InteractionType getInteractionType() {
            return InteractionType.MODAL;
        }
    }

    interface ISelectMenuEntity extends IInteraction<EntitySelectInteractionEvent> {
        @Override
        default InteractionType getInteractionType() {
            return InteractionType.SELECTION_ENTITY;
        }
    }

    interface ISelectMenuString extends IInteraction<StringSelectInteractionEvent> {
        @Override
        default InteractionType getInteractionType() {
            return InteractionType.SELECTION_STRING;
        }
    }
}
