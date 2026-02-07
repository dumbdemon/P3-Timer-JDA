package com.terransky.p3timerjda.utilities.general;

import com.terransky.p3timerjda.utilities.general.configObjects.Colors;

@SuppressWarnings("unused")
public class Config {

    private String token;
    private String name;
    private String description;
    private Colors colors;

    Config() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Colors getColors() {
        return colors;
    }

    public void setColors(Colors colors) {
        this.colors = colors;
    }
}
