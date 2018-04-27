package io.joshking.dronegestures.gesture;

import org.opencv.core.Point;

import io.joshking.dronegestures.drone.DroneController;

public interface Gesture {

    void clearFlag();

    Hand getInitialHand();

    Point getResetPoint();

    void update(Hand hand, DroneController drone);
}
