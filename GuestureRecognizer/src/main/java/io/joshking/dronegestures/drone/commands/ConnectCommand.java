package io.joshking.dronegestures.drone.commands;

public class ConnectCommand extends BaseCommand {

    final String connectionInterface;

    public ConnectCommand(String connectionInterface) {
        // this.connectionInterface = connectionInterface;
        this.connectionInterface = "radio://0/80/1M";
    }

    @Override
    public Class getTypeClass() {
        return getClass();
    }
}

// CrazyflieDroneMsg|{"type": "ConnectCommand", "connectionInterface": "radio://0/80/1M"}
// CrazyflieDroneMsg|{"type": "FigureEightCommand"}
// CrazyflieDroneMsg|{"type": "LandCommand"}
// CrazyflieDroneMsg|{"type": "LeftCommand"}
