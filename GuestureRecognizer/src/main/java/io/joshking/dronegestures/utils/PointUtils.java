package io.joshking.dronegestures.utils;

import java.awt.*;

public class PointUtils {
    public static Point offsetBy(Point point, Dimension offsetBy) {
        return new Point((int) (point.getX() - offsetBy.width),
                         (int) (point.getY() - offsetBy.height));
    }

    public static Point offsetByHalf(Point point, Dimension offsetBy) {
        return new Point((int) (point.getX() - offsetBy.width / 2),
                         (int) (point.getY() - offsetBy.height / 2));
    }
}
