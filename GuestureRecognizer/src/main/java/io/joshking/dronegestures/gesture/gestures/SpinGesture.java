package io.joshking.dronegestures.gesture.gestures;

import org.opencv.core.Point;

import io.joshking.dronegestures.drone.DroneController;
import io.joshking.dronegestures.drone.commands.command.SpinCommand;
import io.joshking.dronegestures.gesture.Gesture;
import io.joshking.dronegestures.gesture.Hand;

public class SpinGesture implements Gesture {
    private final Hand    hand;
    private final Point   point;
    private       boolean hasSentCommand;

    public SpinGesture(Point point, Hand hand) {
        this.point = point;
        this.hand = hand;
    }

    @Override
    public void clearFlag() {
        hasSentCommand = false;
    }

    @Override
    public Hand getInitialHand() {
        return hand;
    }

    @Override
    public Point getResetPoint() {
        return point;
    }

    @Override
    public void update(Hand hand, DroneController drone) {
        if (!hasSentCommand) {
            drone.sendCommand(new SpinCommand());
        }

        hasSentCommand = true;
    }
}
