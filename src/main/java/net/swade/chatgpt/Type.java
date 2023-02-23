package net.swade.chatgpt;

public enum Type {

    SINGLE,
    BROADCAST,
    FULL;

    public static Type getType(String type) {
        switch (type.toLowerCase()) {
            case "single":
                return SINGLE;
            case "broadcast":
                return BROADCAST;
            case "full":
                return FULL;
            default:
                return null;
        }
    }
}