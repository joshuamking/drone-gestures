package io.joshking.dronegestures.gesture.gestures;

import org.opencv.core.Point;

import io.joshking.dronegestures.drone.DroneController;
import io.joshking.dronegestures.drone.commands.command.ChangeAltitudeCommand;
import io.joshking.dronegestures.gesture.Gesture;
import io.joshking.dronegestures.gesture.Hand;

public class ChangeAltitudeGesture implements Gesture {
    private static final int   MIN_THRESHOLD = 100;
    private final        Hand  hand;
    private final        Point resetPoint;

    public ChangeAltitudeGesture(Point resetPoint, Hand hand) {
        this.resetPoint = resetPoint;
        this.hand = hand;
    }

    @Override
    public void clearFlag() {
    }

    @Override
    public Hand getInitialHand() {
        return hand;
    }

    @Override
    public Point getResetPoint() {
        return resetPoint;
    }

    @Override
    public void update(Hand hand, DroneController drone) {
        // if (!hasSentCommand) {
        //
        // }
        //
        // hasSentCommand = true;

        Point  currentCenter  = hand.findCenter();
        double deltaY         = resetPoint.y - currentCenter.y;
        double deltaYAbsolute = Math.abs(deltaY);

        if (deltaYAbsolute >= MIN_THRESHOLD) {
            deltaY = Math.signum(deltaY);
        } else {
            deltaY = 0;
        }
        double speedY = deltaY;

        drone.sendCommand(new ChangeAltitudeCommand(speedY));
    }
}
