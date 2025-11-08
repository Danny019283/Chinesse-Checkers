package Network;

import java.io.Serializable;

public class ServerMessage implements Serializable {

    public enum MessageType {
        PLAYER_DISCONNECTED,
        GAME_OVER
    }

    private final MessageType type;
    private final Object payload;

    public ServerMessage(MessageType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
