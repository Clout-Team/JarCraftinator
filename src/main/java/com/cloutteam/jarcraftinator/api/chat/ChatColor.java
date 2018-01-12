package com.cloutteam.jarcraftinator.api.chat;

public class ChatColor {

    public static final String VALID_COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

    /**
     * Translates a string with a certain character (most commonly used is &amp;) to a Minecraft color coded string.
     * This method uses the default character (&amp;). If you want to specify your own color symbol, you can use {@link ChatColor#translateAlternateColorCodes(char, String) translateAlternateColorCodes(symbol, text)}
     * @param text The text that you want to colorize.
     * @return The colorized text.
     */
    public static String translateAlternateColorCodes(String text){
        return translateAlternateColorCodes('&', text);
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
        char[] input = text.toCharArray();
        for(int i = 0; i < input.length - 1; i++){
            // If this is a color symbol, is the next letter a valid color code symbol
            if(input[i] == symbol && VALID_COLOR_CODES.indexOf(input[i + 1]) > -1){
                // Replace this character with the sectional symbol
                input[i] = '\u00A7';
                // and ensure the next character is lowercase.
                input[i + 1] = Character.toLowerCase(input[i + 1]);
            }
        }

        return new String(input);
    }

}
