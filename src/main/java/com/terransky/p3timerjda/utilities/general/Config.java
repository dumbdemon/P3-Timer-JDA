package com.terransky.p3timerjda.utilities.general;

import com.terransky.p3timerjda.utilities.general.configObjects.Colors;

@SuppressWarnings("unused")
public class Config {

    private String token;
    private Colors colors;

    Config() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Colors getColors() {
        return colors;
    }

    public void setColors(Colors colors) {
        this.colors = colors;
    }
}
