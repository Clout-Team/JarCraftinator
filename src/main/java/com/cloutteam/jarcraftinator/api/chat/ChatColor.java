package com.cloutteam.jarcraftinator.api.chat;

import java.util.regex.Pattern;

public class ChatColor {

    /**
     * Translates a string with a certain character (most commonly used is &amp;) to a Minecraft color coded string.
     * This method uses the default character (&amp;). If you want to specify your own color symbol, you can use {@link ChatColor#translateAlternateColorCodes(char, String) translateAlternateColorCodes(symbol, text)}
     * @param text The text that you want to colorize.
     * @return The colorized text.
     */
    public static String translateAlternateColorCodes(String text){
        return text.replaceAll(Pattern.quote("&"), "\u00A7");
    }

    /**
     * Translates a string with a certain character (most commonly used is &amp;) to a Minecraft color coded string.
     * If you want to just use the default character (&amp;) you can just use {@link ChatColor#translateAlternateColorCodes(String) translateAlternateColorCodes(text)}
     *
     * @param symbol The symbol that you want to replace with the color code symbol (Usually: &)
     * @param text The text that you want to colorize.
     * @return The colorized text.
     */
    public static String translateAlternateColorCodes(char symbol, String text){
        // Replace symbol of choice with Sectional Symbol
        return text.replaceAll(Pattern.quote(String.valueOf(symbol)), "\u00A7");
    }

}
