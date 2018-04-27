package io.joshking.dronegestures.gesture;

import org.opencv.core.Point;

import io.joshking.dronegestures.drone.DroneController;
import io.joshking.dronegestures.drone.commands.FigureEightCommand;

public class FigureEightGesture implements Gesture {
    private boolean hasSentCommand;
    private Hand    initialHand;
    private Point   resetPoint;

    public FigureEightGesture(Point resetPoint, Hand initialHand) {
        this.resetPoint = resetPoint;
        this.initialHand = initialHand;
    }

    @Override
    public void clearFlag() {
        hasSentCommand = false;
    }

    @Override
    public Hand getInitialHand() {
        return initialHand;
    }

    @Override
    public Point getResetPoint() {
        return resetPoint;
    }

    @Override
    public void update(Hand hand, DroneController drone) {
        if (!hasSentCommand) {
            drone.sendCommand(new FigureEightCommand());
        }

        hasSentCommand = true;
    }
}
