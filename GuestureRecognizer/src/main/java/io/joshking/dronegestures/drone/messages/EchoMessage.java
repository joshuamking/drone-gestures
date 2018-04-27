package io.joshking.dronegestures.drone.messages;

public class EchoMessage extends BaseMessage {


    final String message;

    public EchoMessage(String message) {
        this.message = message;
    }

    @Override
    public Class getTypeClass() {
        return getClass();
    }

    @Override
    public String toString() {
        return message;
    }

    public String getMessage() {
        return message;
    }
}
