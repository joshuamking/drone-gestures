package io.joshking.dronegestures.drone.messages;

public class DroneConnectionStatusMessage extends BaseMessage {


    private final String status;

    public DroneConnectionStatusMessage(String status) {
        this.status = status;
    }

    @Override
    public Class getTypeClass() {
        return getClass();
    }

    public ConnectionStatus getStatus() {
        return ConnectionStatus.valueOf(status);
    }

    public enum ConnectionStatus {
        Connected, Disconnected
    }
}
