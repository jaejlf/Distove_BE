package distove.chat.enumerate;

public enum MessageType {
    WELCOME, IMAGE, FILE, VIDEO, TYPING,
    TEXT, MODIFIED, DELETED;

    public static boolean isNotiMessage(MessageType type) {
        return type == WELCOME || type == TYPING;
    }

}