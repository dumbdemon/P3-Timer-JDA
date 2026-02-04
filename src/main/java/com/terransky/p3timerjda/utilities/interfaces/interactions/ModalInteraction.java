package com.terransky.p3timerjda.utilities.interfaces.interactions;

import com.terransky.p3timerjda.utilities.interfaces.IInteraction;
import net.dv8tion.jda.api.modals.Modal;

public abstract class ModalInteraction implements IInteraction.IModal {

    private final String id;
    private final String title;

    protected ModalInteraction(String id, String title) {
        this.id = id;
        this.title = title;
    }

    protected Modal.Builder getBuilder() {
        return Modal.create(id, title);
    }

    public abstract Modal getContructedModal();

    @Override
    public String getName() {
        return id;
    }
}
