package com.terransky.p3timerjda.utilities.managers;

import com.terransky.p3timerjda.utilities.general.InteractionType;
import com.terransky.p3timerjda.utilities.interfaces.interactions.ButtonInteraction;

import java.util.List;
import java.util.Optional;

public class ButtonInteractionManager extends InteractionManager<ButtonInteraction> {

    public ButtonInteractionManager() {
        super(InteractionType.BUTTON);
    }

    @Override
    public Optional<ButtonInteraction> getInteraction(String interactionName) {
        List<ButtonInteraction> interactions;

        interactions = this.interactions.stream()
            .filter(buttonInteraction -> buttonInteraction.followsPattern(interactionName))
            .toList();

        return interactions.isEmpty() ? super.getInteraction(interactionName) : interactions.stream().findFirst();
    }
}
