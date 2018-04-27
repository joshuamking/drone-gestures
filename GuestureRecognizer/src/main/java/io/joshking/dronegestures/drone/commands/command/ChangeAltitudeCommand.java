package io.joshking.dronegestures.drone.commands.command;

import io.joshking.dronegestures.drone.commands.BaseCommand;

@SuppressWarnings("FieldCanBeLocal")
public class ChangeAltitudeCommand extends BaseCommand {

    private double altitude;

    public ChangeAltitudeCommand(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public Class getTypeClass() {
        return getClass();
    }
}
