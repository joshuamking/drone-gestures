package io.joshking.dronegestures.gesture;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Hand {
    private Map<Integer, MatOfPoint> fingers;
    private int                      handCode;

    public Hand(int handCode) {
        this.handCode = handCode;
    }

    public Hand(int handCode, Map<Integer, MatOfPoint> fingers) {
        this.handCode = handCode;
        this.fingers = fingers;
    }

    private Hand() {
        this.handCode = 0;
        this.fingers = new HashMap<>();
    }

    public static Hand of(MatOfPoint one, MatOfPoint two, /*MatOfPoint three, MatOfPoint four,*/ MatOfPoint five) {
        Hand hand = new Hand();
        hand.addFinger(Finger.ONE, one);
        hand.addFinger(Finger.TWO, two);
        // hand.addFinger(Finger.THREE, three);
        // hand.addFinger(Finger.FOUR, four);
        hand.addFinger(Finger.FIVE, five);
        return hand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(handCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hand hand = (Hand) o;
        return handCode == hand.handCode;
    }

    @Override
    public String toString() {
        return "Hand with handCode: " + handCode;
    }

    private void addFinger(int fingerCode, MatOfPoint finger) {
        Optional.ofNullable(finger).ifPresent(fingerPoints -> {
            fingers.put(fingerCode, fingerPoints);
            handCode |= fingerCode;
        });
    }

    public Point findCenter() {
        Collection<MatOfPoint> fingers      = this.fingers.values();
        int                    numOfFingers = fingers.size();
        return fingers.stream()
                .map(MatOfPoint::toList)
                .map(points -> {
                    int numOfPoints = points.size();
                    double x = points.stream()
                            .map(point -> point.x)
                            .reduce(Double::sum)
                            .orElse(0d) / numOfPoints;
                    double y = points.stream()
                            .map(point -> point.y)
                            .reduce(Double::sum)
                            .orElse(0d) / numOfPoints;
                    return new Point(x, y);
                })
                .reduce((point, point2) -> new Point(point.x + point2.x, point.y + point2.y))
                .map(point -> new Point(point.x / numOfFingers, point.y / numOfFingers))
                .orElse(null);
    }

    public int getFingerCount() {
        return fingers.size();
    }

    public double getSize() {
        Collection<MatOfPoint> fingers      = this.fingers.values();
        int                    numOfFingers = fingers.size();
        return fingers.stream()
                .map(MatOfPoint::size)
                .map(Size::area)
                .reduce(Double::sum)
                .map(size -> size / numOfFingers)
                .orElse(0d);
    }

    public void release() {
        fingers.values().forEach(Mat::release);
    }

    public static class Finger {
        static final         byte   FIVE    = 64;
        // static final         byte   FOUR    = 32;
        static final         byte   ONE     = 4;
        // static final         byte   THREE   = 16;
        static final         byte   TWO     = 8;
        static final         byte   ZERO    = 0;
        private static final byte[] FINGERS = {ZERO, ONE, TWO, ZERO, ZERO, FIVE};

        public static int get(int... fingers) {
            if (Objects.isNull(fingers)) {
                return 0;
            }

            int handCode = 0;
            for (int finger : fingers) {
                handCode |= FINGERS[finger];
            }

            return handCode;
        }
    }
}
