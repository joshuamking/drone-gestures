package io.joshking.dronegestures.view;

import org.opencv.core.Point;

import java.awt.*;
import java.util.Optional;

import javax.swing.*;

import io.joshking.dronegestures.gesture.Gesture;
import io.joshking.dronegestures.gesture.Hand;

public class FlightIndicatorPanel extends JPanel {

    private int originX;
    private int originY;
    private int x;
    private int y;
    private int z;

    public FlightIndicatorPanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        System.out.println("x,y,z - " + x + " " + y + " " + z);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.CYAN);
        int width  = getWidth() / 4;
        int height = getHeight() / 4;
        g.fillRect(originX - width / 2, originY - height / 2, width, height);
        g.setColor(Color.GRAY);
        int x = this.x;
        int y = this.y;
        int z = Math.max(20, 30 + this.z);
        g.fillOval(x, y, z, z);

    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void updateData(Hand hand, Gesture gesture, int frameWidth) {
        Point resetPoint = gesture.getResetPoint();

        originX = Optional.ofNullable(resetPoint)
                .map(point -> point.x - 80)
                .map(d -> d / (frameWidth - 120))
                .map(d -> d * getWidth())
                .orElse(0d)
                .intValue();
        originY = Optional.ofNullable(resetPoint)
                .map(point -> point.y - 80)
                .map(d -> d / (frameWidth - 120))
                .map(d -> d * getHeight())
                .orElse(0d)
                .intValue();

        org.opencv.core.Point handCenter = hand.findCenter();

        setX(Optional.ofNullable(handCenter)
                     .map(point -> point.x - 80)
                     .map(d -> d / (frameWidth - 120))
                     .map(d -> d * getWidth())
                     .orElse(0d)
                     .intValue());
        setY(Optional.ofNullable(handCenter)
                     .map(point -> point.y - 80)
                     .map(d -> d / (frameWidth - 120))
                     .map(d -> d * getHeight())
                     .orElse(0d)
                     .intValue());
        setZ((int) (hand.getSize() * hand.getSize() / 1000));

        repaint();
    }
}
