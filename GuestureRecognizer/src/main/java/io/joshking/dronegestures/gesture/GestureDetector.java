package io.joshking.dronegestures.gesture;

import org.opencv.core.Point;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.joshking.dronegestures.drone.DroneController;
import io.joshking.dronegestures.gesture.gestures.BackGesture;
import io.joshking.dronegestures.gesture.gestures.ForwardGesture;
import io.joshking.dronegestures.gesture.gestures.LandGesture;
import io.joshking.dronegestures.gesture.gestures.LeftGesture;
import io.joshking.dronegestures.gesture.gestures.RightGesture;
import io.joshking.dronegestures.gesture.gestures.SpinGesture;

public class GestureDetector {
    private static final String                        QR_ENCODED_TEXT = "1234567890";
    private static final long                          TIMEOUT_MILLS   = 8000;
    private              Gesture                       currentlyTrackingGesture;
    private              DroneController               drone;
    private              HandListener                  handListener;
    private              HashMap<Hand, Consumer<Hand>> handToGestureMap;
    private              boolean                       isFlying;
    private              long                          lastGestureDetected;

    public GestureDetector(DroneController drone) {
        this.drone = drone;
        handToGestureMap = new HashMap<>();

        // LEFT
        handToGestureMap.put(new Hand(Hand.Finger.get(1)),
                             hand -> trackGesture(hand, LeftGesture.class, LeftGesture::new));

        // RIGHT
        handToGestureMap.put(new Hand(Hand.Finger.get(5)),
                             hand -> trackGesture(hand, RightGesture.class, RightGesture::new));

        // FORWARD
        handToGestureMap.put(new Hand(Hand.Finger.get(2, 5)),
                             hand -> trackGesture(hand, ForwardGesture.class, ForwardGesture::new));

        // BACK
        handToGestureMap.put(new Hand(Hand.Finger.get(1, 5)),
                             hand -> trackGesture(hand, BackGesture.class, BackGesture::new));

        // SPIN
        handToGestureMap.put(new Hand(Hand.Finger.get(1, 2)),
                             hand -> trackGesture(hand, SpinGesture.class, SpinGesture::new));

        // ALTITUDE
        // handToGestureMap.put(new Hand(Hand.Finger.get(2)), hand -> trackGesture(hand, FigureEightGesture.class, FigureEightGesture::new));

        // LAND
        handToGestureMap.put(new Hand(Hand.Finger.get(1, 2, 5)),
                             hand -> trackGesture(hand, LandGesture.class, LandGesture::new));
    }

    public boolean detectGesture(Hand hand, Supplier<BufferedImage> imageSupplier) {
        // if (isFlying) {
        handToGestureMap.getOrDefault(hand, ignored -> nothingDetected()).accept(hand);
        // } else {
        // isTrackingGestures = hand.getFingerCount() == 3;
        if (!isFlying) {
            isFlying = drone.isFlying();
        }
        // }

        return isFlying;
    }

    private void nothingDetected() {
        if (isFlying && currentlyTrackingGesture != null && System.currentTimeMillis() - lastGestureDetected > TIMEOUT_MILLS) {
            drone.land();
            isFlying = false;
            currentlyTrackingGesture = null;
        } else if (isFlying) {
            drone.halt();
            if (currentlyTrackingGesture != null) {
                currentlyTrackingGesture.clearFlag();
            }
        }
    }

    public void setHandListener(HandListener handListener) {
        this.handListener = handListener;
    }

    private <T extends Gesture> void trackGesture(Hand hand, Class<T> gesture, BiFunction<Point, Hand, T> gestureSupplier) {
        lastGestureDetected = System.currentTimeMillis();
        isFlying = true;
        if (currentlyTrackingGesture == null || !currentlyTrackingGesture.getClass()
                .equals(gesture)) {
            currentlyTrackingGesture = gestureSupplier.apply(hand.findCenter(), hand);
        }

        Optional.ofNullable(handListener)
                .ifPresent(listener -> listener.handMoved(hand, currentlyTrackingGesture));

        currentlyTrackingGesture.update(hand, drone);
    }


}
