package io.joshking.dronegestures.drone;

import org.apache.commons.math.geometry.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.joshking.dronegestures.drone.commands.BaseCommand;
import io.joshking.dronegestures.drone.commands.MoveCommand;
import io.joshking.dronegestures.drone.commands.command.HaltCommand;
import io.joshking.dronegestures.drone.commands.command.LandCommand;
import io.joshking.dronegestures.drone.commands.command.TakeOffCommand;
import io.joshking.dronegestures.drone.connection.DroneConnection;
import io.joshking.dronegestures.utils.MsgBox;
import io.joshking.dronegestures.utils.ProjectUtils;

public class DroneController {
    private static final Logger          log = LoggerFactory.getLogger(DroneController.class);
    private final        MsgBox          msgBox;
    private              DroneConnection droneConnection;
    private              boolean         isFlying;

    public DroneController(Runnable connectedCallback, Runnable disconnectedCallback) {
        log.info("Starting Python bridge...");

        msgBox = new MsgBox();
        msgBox.showMsg("Connecting to the drone...", Integer.MAX_VALUE);

        connect(connectedCallback, disconnectedCallback);
    }

    public DroneController() {
        this(() -> {
        }, () -> System.exit(2));
    }

    public void accel(Vector3D direction) {
        System.out.println(String.format("Move drone with accel: %s", direction.toString()));
        // droneConnection.sendCommandToDrone(new MoveCommand(direction));
    }

    public void connect(Runnable connectedCallback, Runnable disconnectedCallback) {
        droneConnection = new DroneConnection(() -> {
            log.debug("Drone connected");
            try {
                isFlying = true;
                connectedCallback.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            msgBox.showMsg("Connected to drone!", MsgBox.DEFAULT_MSG_TIME);
            ProjectUtils.delayRunnable(() -> msgBox.showMsg("Watching for commands!"),
                                       MsgBox.DEFAULT_MSG_TIME - 100);
        }, () -> {
            log.error("DRONE DISCONNECTED!");
            msgBox.showMsg("DRONE DISCONNECTED!", 2000);
            ProjectUtils.delayRunnable(() -> msgBox.showMsg("Searching for drone...", 10000), 1000);
            try {
                disconnectedCallback.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, this::isFlying);
    }

    public void die() {
        System.out.println("DIE!");
        isFlying = false;
        // droneConnection.sendCommandToDrone(new MoveCommand());
    }

    public void editDrift(double x, int y, double z) {
        MoveCommand.driftRoll += x;
        MoveCommand.driftThrust += y;
        MoveCommand.driftPitch += z;
    }

    public void halt() {
        droneConnection.sendCommandToDrone(new HaltCommand());
    }


    public boolean isFlying() {
        return isFlying;
    }

    // public void glide() {
    //     System.out.println("GLIDE!");
    //     isFlying = true;
    //     droneConnection.sendCommandToDrone(new MoveCommand(0, 35800, 0));
    // }

    public void land() {
        if (isFlying) {
            msgBox.showMsg("Landing...");
            System.out.println("LAND!");

            droneConnection.sendCommandToDrone(new LandCommand());

            isFlying = false;
        }
    }

    public void sendCommand(BaseCommand command) {
        droneConnection.sendCommandToDrone(command);
    }

    public void takeOff() {
        if (!isFlying) {
            msgBox.showMsg("Take off...");
            System.out.println("TAKE OFF!");
            isFlying = true;

            droneConnection.sendCommandToDrone(new TakeOffCommand());
        }
    }

    public void yaw(double yaw) {
        // droneConnection.sendCommandToDrone(new MoveCommand(yaw));
    }
}
