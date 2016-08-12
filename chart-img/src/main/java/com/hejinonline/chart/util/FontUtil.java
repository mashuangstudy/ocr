package com.hejinonline.chart.util;

import java.awt.*;

/**
 * Created by zhangyayun on 16-6-20.
 */
public class FontUtil {

    private static final String[] fonts = {"SIMSUN","方正兰亭宋_GBK","方正细黑一简体","方正细黑一简体","汉仪书宋一简","华文仿宋","华文宋体","华文中宋"};

    public static Font getRandomFont(String str) {
        Font font;

        do {
            int fontIndex = RandomUtil.getRandomInt(0, fonts.length - 1);
            int fontSize = RandomUtil.getRandomInt(108, 128);
            font = new Font(fonts[fontIndex], Font.PLAIN, fontSize);
        } while (font.canDisplayUpTo(str) == 0);

        return font;
    }

    public static Font getRandomFont(String str, int lowSize, int highSize) {
        Font font;

        do {
            int fontIndex = RandomUtil.getRandomInt(0, fonts.length - 1);
            int fontSize = RandomUtil.getRandomInt(lowSize, highSize);
            font = new Font(fonts[fontIndex], Font.PLAIN, fontSize);
        } while (font.canDisplayUpTo(str) == 0);

        return font;
    }

}
