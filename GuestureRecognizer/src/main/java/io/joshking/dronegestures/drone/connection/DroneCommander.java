package io.joshking.dronegestures.drone.connection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import io.joshking.dronegestures.drone.commands.BaseCommand;
import io.joshking.dronegestures.utils.GsonUtils;

public class DroneCommander extends ThreadLoop {
    private final BufferedWriter     writer;
    private       Queue<BaseCommand> cmdQueue;
    private       Supplier<Boolean>  isDroneFlying;

    public DroneCommander(OutputStream outputStream, Supplier<Boolean> isDroneFlying) {
        super(DroneCommander.class.getSimpleName());

        this.isDroneFlying = isDroneFlying;
        this.cmdQueue = new ConcurrentLinkedQueue<>();
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    @Override
    public void close() {

    }

    @Override
    public void loop() {
        if (cmdQueue.isEmpty()) {
            return;
        }

        Optional.ofNullable(cmdQueue.poll())
                .map(GsonUtils.GSON::toJson)
                .map(s -> s.replace("'", "\""))
                .ifPresent(s -> {
                    try {
                        writer.write(DroneConnection.DRONE_MSG_PREFIX);
                        writer.write(s);
                        writer.newLine();
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void sendCommand(BaseCommand command) {
        if (isDroneFlying.get()) {
            cmdQueue.add(command);
        }
    }
}
