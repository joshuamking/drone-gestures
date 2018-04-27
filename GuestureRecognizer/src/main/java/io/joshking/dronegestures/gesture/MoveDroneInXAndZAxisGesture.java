package io.joshking.dronegestures.gesture;

import org.apache.commons.math.geometry.Vector3D;
import org.opencv.core.Point;

import io.joshking.dronegestures.drone.DroneController;

public class MoveDroneInXAndZAxisGesture implements Gesture {
    private static final int    MIN_THRESHOLD = 100;
    private static final double SENSITIVITY   = 150;    // Smaller the number (greater than 1), the more sensitive it is
    private              Hand   initialHand;
    private              Point  resetPoint;

    public MoveDroneInXAndZAxisGesture(Point resetPoint, Hand initialHand) {
        this.resetPoint = resetPoint;
        this.initialHand = initialHand;
    }

    @Override
    public void clearFlag() {

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

        Point currentCenter = hand.findCenter();

        double deltaX         = resetPoint.x - currentCenter.x;
        double deltaY         = resetPoint.y - currentCenter.y;
        double deltaXAbsolute = Math.abs(deltaX);
        double deltaYAbsolute = Math.abs(deltaY);

        System.out.println(deltaXAbsolute);
        System.out.println(deltaYAbsolute);

        if (deltaXAbsolute <= MIN_THRESHOLD) {
            deltaX = Math.signum(deltaX) * Math.max(deltaXAbsolute, deltaXAbsolute - MIN_THRESHOLD);
        }
        if (deltaYAbsolute <= MIN_THRESHOLD) {
            deltaY = Math.signum(deltaY) * Math.max(deltaYAbsolute, deltaYAbsolute - MIN_THRESHOLD);
        }

        double speedX = deltaX / SENSITIVITY;
        double speedY = deltaY / SENSITIVITY;
        // double speedY = deltaY * 0.07 / 9;

        // Math.signum(deltaY)
        // More sensitive going up, less going down....?


        System.out.println("speedX: " + speedX);

        drone.accel(new Vector3D(speedX, 1.2, speedY));
        // drone.die();
    }


}
