package distove.chat.enumerate;

public enum MessageType {

    WELCOME, IMAGE, FILE, VIDEO, TYPING,
    TEXT, MODIFIED, DELETED;

    public static boolean isFileType(MessageType type) {
        return type == IMAGE || type == FILE || type == VIDEO;
    }

}