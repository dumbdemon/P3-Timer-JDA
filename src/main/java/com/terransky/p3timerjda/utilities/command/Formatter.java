package com.terransky.p3timerjda.utilities.command;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Formatter {
    private static final NavigableMap<Float, String> suffixes = new TreeMap<>() {{
        put(1e3f, " Thousand");
        put(1e6f, " Million");
        put(1e9f, " Billion");
        put(1e12f, " Trillion");
        put(1e15f, " Quadrillion");
        put(1e18f, " Quintillion");
        put(1e21f, " Sextillion");
        put(1e24f, " Septillion");
        put(1e27f, " Octillion");
        put(1e30f, " Nonillion");
        put(1e33f, " Decillion");
        put(1e36f, " Undecillion");
    }};

    /**
     * Makes extra large numbers look nice~ <br />
     * Code Courtesy of <a href="https://stackoverflow.com/a/30661479">assylias's answer</a> on stackoverflow.
     *
     * @return {@link String} that contains a two-point decimal and the scale of value.
     */
    public static String largeNumberFormat(float value) {
        DecimalFormat simpleNum = new DecimalFormat("#.##");

        if (value == Float.MIN_VALUE) return largeNumberFormat(Float.MIN_VALUE + 1);
        if (value < 0) return "-" + largeNumberFormat(-value);
        if (value < 1000) return Float.toString(value);

        Map.Entry<Float, String> e = suffixes.floorEntry(value);
        float divideBy = e.getKey();
        String suffix = e.getValue();

        float truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? simpleNum.format(truncated / 10d) + suffix : simpleNum.format(truncated / 10) + suffix;
    }

    @NotNull
    public static Section getLinkButtonSection(String link, String message) {
        return Section.of(
            Button.link(
                link,
                Emoji.fromUnicode("U+1F517")
            ),
            TextDisplay.ofFormat(message)
        );
    }

    @NotNull
    public static Section getLinkButtonSection(String link, String message, Emoji emoji) {
        return Section.of(
            Button.link(
                link,
                emoji
            ),
            TextDisplay.ofFormat(message)
        );
    }

    @NotNull
    public static String percentEncode(String s) {
        return percentEncode(s, StandardCharsets.UTF_8);
    }

    @NotNull
    public static String percentEncode(String s, Charset encoding) {
        if (s == null) {
            return "";
        }
        return URLEncoder.encode(s, encoding)
            .replaceAll("\\[", "%5B")
            .replaceAll("]", "%5D")
            .replaceAll("\\+|\\s", "%20")
            .replaceAll("\\*", "%2A")
            .replaceAll("%7E", "~");
    }

    public static String getNameOfClass(@NotNull Class<?> clazz) {
        String[] className = clazz.getName().split("\\.");
        return className[className.length - 1];
    }
}
