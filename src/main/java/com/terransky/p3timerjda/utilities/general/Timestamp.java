package com.terransky.p3timerjda.utilities.general;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Date;

@SuppressWarnings("unused")
public enum Timestamp {
    /**
     * <b>EX:</b> {@literal <}t:1673461755:D{@literal >}<br/>
     * <b>On Discord:</b> January 11, 2023
     */
    LONG_DATE('D'),
    /**
     * <b>EX:</b> {@literal <}t:1673461755:F{@literal >}<br/>
     * <b>On Discord:</b> Wednesday, January 11, 2023, at 12:29 PM
     */
    LONG_DATE_W_DoW_SHORT_TIME('F'),
    /**
     * <b>EX:</b> {@literal <}t:1673461755:f{@literal >}<br/>
     * <b>On Discord:</b> January 11, 2023, at 12:29 PM
     */
    LONG_DATE_W_SHORT_TIME('f'),
    /**
     * <b>EX:</b> {@literal <}t:1673461755:t{@literal >}<br/>
     * <b>On Discord:</b> 12:29 PM
     */
    LONG_TIME('T'),
    /**
     * Will show the difference between the current time and the Markdown time
     */
    RELATIVE('R'),
    /**
     * <b>EX:</b> {@literal <}t:1673461755:d{@literal >}<br/>
     * <b>On Discord:</b> 01/11/2023
     */
    SHORT_DATE('d'),
    /**
     * <b>EX:</b> {@literal <}t:1673461755:T{@literal >}<br/>
     * <b>On Discord:</b> 12:29:15 PM
     */
    SHORT_TIME('t');

    private final Character code;

    Timestamp(Character code) {
        this.code = code;
    }

    /**
     * Get a timestamp Markdown code using an {@link OffsetDateTime}.
     *
     * @param offsetDateTime An {@link OffsetDateTime}.
     * @param timestamp      A {@link Timestamp} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(@NotNull OffsetDateTime offsetDateTime, @NotNull Timestamp timestamp) {
        return getDateAsTimestamp(offsetDateTime.toEpochSecond(), timestamp);
    }

    /**
     * Get a timestamp Markdown code using an {@link OffsetDateTime}.<br/>
     * This will use the enum {@link Timestamp#LONG_DATE_W_DoW_SHORT_TIME} for the Markdown.
     *
     * @param offsetDateTime An {@link OffsetDateTime}.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(@NotNull OffsetDateTime offsetDateTime) {
        return getDateAsTimestamp(offsetDateTime.toEpochSecond());
    }

    /**
     * Get a timestamp Markdown code for a specific date.
     *
     * @param date      A {@link Date} to parse.
     * @param timestamp A {@link Timestamp} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(@NotNull Date date, @NotNull Timestamp timestamp) {
        return getDateAsTimestamp(date.toInstant().getEpochSecond(), timestamp);
    }

    /**
     * Get a timestamp Markdown code for a specific date.<br/>
     * This will use the enum {@link Timestamp#LONG_DATE_W_DoW_SHORT_TIME} for the Markdown.
     *
     * @param date A {@link Date} to parse.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(@NotNull Date date) {
        return getDateAsTimestamp(date.toInstant().getEpochSecond());
    }

    /**
     * Get a timestamp Markdown code using epoch seconds.
     *
     * @param epochSeconds Epoch seconds. NOTE: this <b>must</b> be in seconds.
     * @param timestamp    A {@link Timestamp} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(long epochSeconds, @NotNull Timestamp timestamp) {
        return "<t:%d:%s>".formatted(epochSeconds, timestamp.getCode());
    }

    /**
     * Get a timestamp Markdown code using epoch seconds.<br/>
     * This will use the enum {@link Timestamp#LONG_DATE_W_DoW_SHORT_TIME} for the Markdown.
     *
     * @param epochSeconds Epoch seconds. NOTE: this <b>must</b> be in seconds.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(long epochSeconds) {
        return getDateAsTimestamp(epochSeconds, LONG_DATE_W_DoW_SHORT_TIME);
    }

    public Character getCode() {
        return code;
    }
}
