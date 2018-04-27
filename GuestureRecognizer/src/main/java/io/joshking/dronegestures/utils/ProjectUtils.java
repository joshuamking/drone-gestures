package io.joshking.dronegestures.utils;

public class ProjectUtils {

    private ProjectUtils() {
    }

    public static void delayRunnable(Runnable action, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            action.run();
        }, "Delayed Runnable").start();
    }
}
