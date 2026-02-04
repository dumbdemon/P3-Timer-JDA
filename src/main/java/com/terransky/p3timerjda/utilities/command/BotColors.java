package com.terransky.p3timerjda.utilities.command;

import com.terransky.p3timerjda.P3TimerJDA;

import java.awt.*;
import java.util.regex.Pattern;

public enum BotColors {

    DEFAULT(P3TimerJDA.getConfig().getColors().getMain()),
    SUB_DEFAULT(P3TimerJDA.getConfig().getColors().getSub()),
    ERROR(P3TimerJDA.getConfig().getColors().getError());

    private final Color color;

    BotColors(String hexCode) {
        String HEX_TRIPLET_REGEX = "^(([0-9a-fA-F]{2}){3}|([0-9a-fA-F]){3})$";
        Pattern HEX_TRIPLET_PATTERN = Pattern.compile(HEX_TRIPLET_REGEX);
        if (HEX_TRIPLET_PATTERN.matcher(hexCode).matches())
            switch (hexCode.length()) {
                case 3, 4 -> {
                    int offset = 0;
                    if (hexCode.charAt(0) == '#') offset++;
                    String R = String.valueOf(hexCode.charAt(offset));
                    String G = String.valueOf(hexCode.charAt(1 + offset));
                    String B = String.valueOf(hexCode.charAt(2 + offset));
                    this.color = Color.decode("#" + R + R + G + G + B + B);
                }
                case 6 -> this.color = Color.decode('#' + hexCode);
                default -> this.color = Color.decode(hexCode);
            }
        else this.color = Color.WHITE;
    }

    public Color getColor() {
        return color;
    }
}
