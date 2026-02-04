package com.terransky.p3timerjda.utilities.interfaces.interactions;


import com.terransky.p3timerjda.utilities.interfaces.IInteraction;

public abstract class SelectMenuStringInteraction implements IInteraction.ISelectMenuString {

    private final String id;

    protected SelectMenuStringInteraction(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }
}
