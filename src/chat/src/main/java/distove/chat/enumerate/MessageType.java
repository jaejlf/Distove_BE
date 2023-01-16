package distove.chat.enumerate;

public enum MessageType {
    WELCOME, IMAGE, FILE, VIDEO, TYPING,
    TEXT, MODIFIED, DELETED;

    public static boolean canUpdate(MessageType type) {
        return type == TEXT || type == MODIFIED || type == DELETED;
    }

}