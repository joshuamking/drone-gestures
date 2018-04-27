package io.joshking.dronegestures.drone.commands;

@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
public class FigureEightCommand extends BaseCommand {


    public FigureEightCommand() {

    }

    @Override
    public Class getTypeClass() {
        return getClass();
    }
}

// CrazyflieDroneMsg|{"type": "MoveCommand", "roll": 0, "thrust": 4000, "pitch": 0}
