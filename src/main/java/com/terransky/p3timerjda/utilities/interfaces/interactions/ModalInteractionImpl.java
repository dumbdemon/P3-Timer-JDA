package com.terransky.p3timerjda.utilities.interfaces.interactions;

import net.dv8tion.jda.api.modals.Modal;

public abstract class ModalInteractionImpl<T> extends ModalInteraction {

    protected ModalInteractionImpl(String id, String title) {
        super(id, title);
    }

    @Override
    public Modal getContructedModal() {
        throw new IllegalArgumentException("No object provided.");
    }

    public abstract Modal getContructedModal(T t);
}
