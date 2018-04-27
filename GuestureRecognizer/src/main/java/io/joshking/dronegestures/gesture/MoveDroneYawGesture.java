package io.joshking.dronegestures.gesture;

import org.opencv.core.Point;

import io.joshking.dronegestures.drone.DroneController;

public class MoveDroneYawGesture implements Gesture {
    @SuppressWarnings("FieldCanBeLocal") private static double SENSITIVITY = 0.5;
    private                                             Hand   hand;
    private                                             Point  resetPoint;

    public MoveDroneYawGesture(Point resetPoint, Hand hand) {
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
        Point  currentCenter = hand.findCenter();
        double deltaYaw      = resetPoint.x - currentCenter.x;

        double speedYaw = deltaYaw * SENSITIVITY;
        drone.yaw(speedYaw);
    }
}
