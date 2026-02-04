package com.terransky.p3timerjda.utilities.interfaces.interactions;


import com.terransky.p3timerjda.utilities.interfaces.IInteraction;

public abstract class SelectMenuEntityInteraction implements IInteraction.ISelectMenuEntity {

    private final String id;

    protected SelectMenuEntityInteraction(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }
}
