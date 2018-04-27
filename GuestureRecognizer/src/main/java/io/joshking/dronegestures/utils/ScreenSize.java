package io.joshking.dronegestures.utils;

import java.awt.*;

public class ScreenSize extends Dimension {
    public ScreenSize() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        int width  = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        this.setSize(width, height);
    }

    public Point getCenter() {
        return new Point((int) (getWidth() / 2), (int) (getHeight() / 2));
    }

    public int getHeightInt() {
        return height;
    }

    public int getWidthInt() {
        return this.width;
    }
}
