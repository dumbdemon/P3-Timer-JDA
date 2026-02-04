package com.terransky.p3timerjda.utilities.interfaces.interactions;

import com.terransky.p3timerjda.utilities.interfaces.IInteraction;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;

import java.util.Random;
import java.util.regex.Pattern;

public abstract class ButtonInteraction implements IInteraction.IButton {

    private final String name;
    private final Pattern pagePattern;

    protected ButtonInteraction(String name) {
        this(name, null);
    }

    protected ButtonInteraction(String name, Pattern pagePattern) {
        this.name = name;
        this.pagePattern = pagePattern;
    }

    public String getButtonId(int page, String identifier) {
        return "";
    }

    public Button getButton(ButtonStyle style, String label) {
        return getButton(style, label, false);
    }

    public Button getButton(String label) {
        return getButton(ButtonStyle.PRIMARY, label);
    }

    public Button getButton(ButtonStyle style, String label, boolean disabled) {
        return new ButtonImpl(disabled ? "disabled-button" + new Random().nextInt(512) : getName(), label, style, disabled, null);
    }

    public Button getButton(String label, boolean disabled) {
        return getButton(ButtonStyle.PRIMARY, label, disabled);
    }

    public Button getButton(ButtonStyle style, int page, String identifier, String label, boolean disabled) {
        return new ButtonImpl(getButtonId(page, identifier), label, style, disabled, null);
    }

    public Button getButton(ButtonStyle style, int page, String identifier, String label) {
        return new ButtonImpl(getButtonId(page, identifier), label, style, false, null);
    }

    public Button getButton(int page, String identifier, String label, boolean disabled) {
        return getButton(ButtonStyle.PRIMARY, page, identifier, label, disabled);
    }

    public Button getButton(int page, String identifier, String label) {
        return getButton(ButtonStyle.PRIMARY, page, identifier, label);
    }

    public boolean followsPattern(String name) {
        if (pagePattern == null)
            return false;

        return pagePattern.matcher(name).matches();
    }

    @Override
    public String getName() {
        return name;
    }
}
