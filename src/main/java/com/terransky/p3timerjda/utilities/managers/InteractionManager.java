package com.terransky.p3timerjda.utilities.managers;

import com.terransky.p3timerjda.utilities.general.InteractionType;
import com.terransky.p3timerjda.utilities.interfaces.IInteraction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;

public class InteractionManager<T extends IInteraction<?>> {
    private final InteractionType interactionType;
    protected HashSet<T> interactions = new HashSet<>();

    protected InteractionManager(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    protected final void addInteraction(@NotNull T interaction) {
        if (interaction.getInteractionType().hasDedicatedManager() && interactionType != interaction.getInteractionType()) {
            LoggerFactory.getLogger(getClass())
                .error("Interaction has dedicated manager. Please us that manager instead", new IllegalArgumentException());
        } else interactions.add(interaction);
    }

    public Optional<T> getInteraction(String interactionName) {
        return interactions.stream()
            .filter(interaction -> interaction.getName().equalsIgnoreCase(interactionName))
            .findFirst();
    }
}
