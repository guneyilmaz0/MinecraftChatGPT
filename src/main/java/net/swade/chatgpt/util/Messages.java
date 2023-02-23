package net.swade.chatgpt.util;

public class Messages {
    public static String format(String str) {
        if (str == null) return "Error - Bad Config";
        return str.replace("&", "§");
    }
}
