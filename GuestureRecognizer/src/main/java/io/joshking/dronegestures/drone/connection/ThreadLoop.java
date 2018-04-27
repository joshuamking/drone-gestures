package io.joshking.dronegestures.drone.connection;

import java.io.IOException;

public abstract class ThreadLoop extends Thread {
    ThreadLoop(String name) {
        super(name);
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                loop();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    abstract void close();

    abstract void loop() throws InterruptedException, IOException;
}
