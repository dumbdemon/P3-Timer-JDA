package com.terransky.p3timerjda.utilities.command;

import com.terransky.p3timerjda.utilities.interfaces.interactions.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SlashCommandMetadata implements Comparable<SlashCommandMetadata> {

    private final String name;
    private final String description;
    private final boolean isNSFW;

    public SlashCommandMetadata(@NotNull SlashCommandInteraction interaction) {
        this.name = interaction.getNameReadable();
        this.description = interaction.getDescription();
        this.isNSFW = interaction.isNSFW();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    @Override
    public int compareTo(@NotNull SlashCommandMetadata o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SlashCommandMetadata that = (SlashCommandMetadata) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription());
    }
}
