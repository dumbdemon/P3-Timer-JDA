package com.terransky.p3timerjda.utilities.exceptions;

import java.io.IOException;

@SuppressWarnings("unused")
public class DiscordAPIException extends IOException {

    public DiscordAPIException() {
    }

    public DiscordAPIException(String message) {
        super(message);
    }

    public DiscordAPIException(Throwable cause) {
        super(cause);
    }

    public DiscordAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
