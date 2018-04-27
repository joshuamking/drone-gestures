package io.joshking.dronegestures.gesture;

public interface HandListener {
    void handMoved(Hand hand, Gesture currentlyTrackingGesture);
}
