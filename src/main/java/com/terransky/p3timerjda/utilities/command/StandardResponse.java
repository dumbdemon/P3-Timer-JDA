package com.terransky.p3timerjda.utilities.command;

import com.terransky.p3timerjda.utilities.interfaces.IInteraction;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StandardResponse {

    StandardResponse() {
    }

    @NotNull
    public static Container getResponseContainer(String title, List<ContainerChildComponent> components, @NotNull Color color) {
        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.ofFormat("# %s", title));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.addAll(components);

        return Container.of(children).withAccentColor(color);
    }

    @NotNull
    public static Container getResponseContainer(String title, ContainerChildComponent component, @NotNull Color color) {
        return getResponseContainer(title, List.of(component), color);
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, List<ContainerChildComponent> components, Color color) {
        return getResponseContainer(interaction.getNameReadable(), components, color);
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, ContainerChildComponent component, Color color) {
        return getResponseContainer(interaction.getNameReadable(), component, color);
    }

    @NotNull
    public static Container getResponseContainer(String title, List<ContainerChildComponent> components, @NotNull BotColors botColors) {
        return getResponseContainer(title, components, botColors.getColor());
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, List<ContainerChildComponent> components) {
        return getResponseContainer(interaction.getNameReadable(), components, BotColors.DEFAULT);
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, List<ContainerChildComponent> components, BotColors botColors) {
        return getResponseContainer(interaction.getNameReadable(), components, botColors);
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, ContainerChildComponent component) {
        return getResponseContainer(interaction, List.of(component));
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, ContainerChildComponent component, BotColors botColors) {
        return getResponseContainer(interaction, List.of(component), botColors);
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, String message) {
        return getResponseContainer(interaction, TextDisplay.of(message));
    }

    @NotNull
    public static <T extends IInteraction<?>> Container getResponseContainer(@NotNull T interaction, String message, BotColors botColors) {
        return getResponseContainer(interaction, TextDisplay.of(message), botColors);
    }

    @NotNull
    public static Container getResponseContainer(String title, List<ContainerChildComponent> components) {
        return getResponseContainer(title, components, BotColors.DEFAULT);
    }

    @NotNull
    public static Container getResponseContainer(String title, ContainerChildComponent component) {
        return getResponseContainer(title, List.of(component), BotColors.DEFAULT);
    }

    @NotNull
    public static Container getResponseContainer(String title, ContainerChildComponent component, BotColors botColors) {
        return getResponseContainer(title, List.of(component), botColors);
    }

    @NotNull
    public static Container getResponseContainer(String title, String message) {
        return getResponseContainer(title, TextDisplay.of(message));
    }

    @NotNull
    public static Container getResponseContainer(String title, String message, BotColors botColors) {
        return getResponseContainer(title, TextDisplay.of(message), botColors);
    }
}
