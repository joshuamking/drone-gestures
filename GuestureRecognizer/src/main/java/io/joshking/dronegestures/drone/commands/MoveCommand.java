package io.joshking.dronegestures.drone.commands;

import org.apache.commons.math.geometry.Vector3D;

@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
public class MoveCommand extends BaseCommand {

    public static double driftPitch  = 0;
    public static double driftRoll   = 0;
    public static double driftThrust = 0;

    private double pitch;
    private double roll;
    private double thrust;
    private double yaw;

    public MoveCommand(Vector3D direction) {
        this(direction.getX(), direction.getY(), direction.getZ());

        this.roll *= 0.75;
        this.pitch *= 0.75;

        // if (this.thrust >= 2000 && this.thrust <= 35800) {
        // this.thrust = 35800;
        // }

        this.thrust = Math.min(this.thrust, 2);
        this.thrust = Math.max(this.thrust, 0.2);
    }

    public MoveCommand() {
        this(0, 0, 0);
    }

    public MoveCommand(double yaw) {
        this(0, 1, 0);
        this.yaw = yaw;
    }

    public MoveCommand(double roll, double thrust, double pitch) {
        this.roll = driftRoll + roll;
        this.thrust = driftThrust + thrust;
        this.pitch = driftPitch + pitch;
    }

    @Override
    public Class getTypeClass() {
        return getClass();
    }
}

// CrazyflieDroneMsg|{"type": "MoveCommand", "roll": 0, "thrust": 4000, "pitch": 0}
