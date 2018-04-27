package io.joshking.dronegestures.drone.connection;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import io.joshking.dronegestures.drone.commands.BaseCommand;
import io.joshking.dronegestures.drone.commands.ConnectCommand;
import io.joshking.dronegestures.drone.messages.AvailableInterfacesMessage;
import io.joshking.dronegestures.drone.messages.BaseMessage;
import io.joshking.dronegestures.drone.messages.DroneConnectionStatusMessage;

public class DroneConnection extends Thread {

    public static final String             PYTHON_GREETING  = "Greetings from the Python bridge!";
    static final        String             DRONE_MSG_PREFIX = "CrazyflieDroneMsg|";
    private final       Queue<BaseMessage> msgQueue;
    private             Runnable           connectedCallback;
    private             Runnable           disconnectedCallback;
    private             DroneCommander     droneCommander;
    private             DroneListener      droneListener;
    private             Supplier<Boolean>  isDroneFlying;

    public DroneConnection(Runnable connectedCallback, Runnable disconnectedCallback, Supplier<Boolean> isDroneFlying) {
        super(DroneConnection.class.getSimpleName());

        this.connectedCallback = connectedCallback;
        this.disconnectedCallback = disconnectedCallback;
        this.isDroneFlying = isDroneFlying;
        this.msgQueue = new ConcurrentLinkedQueue<>();
        this.start();
    }

    @Override
    public void run() {
        try {
            Process process = spawnPythonScript();
            Thread.sleep(1000);
            droneListener = new DroneListener(msgQueue::add,
                                              process.getInputStream(),
                                              process.getErrorStream());
            droneCommander = new DroneCommander(process.getOutputStream(), isDroneFlying);
            droneListener.waitForString(PYTHON_GREETING);

            droneListener.start();
            droneCommander.start();

            while (process.isAlive()) {
                if (msgQueue.isEmpty()) {
                    continue;
                }

                handleDroneMsg(msgQueue.poll());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        droneListener.close();
        droneCommander.close();
        disconnectedCallback.run();
    }

    private void handleDroneMsg(BaseMessage message) {
        if (message instanceof AvailableInterfacesMessage) {
            AvailableInterfacesMessage availableInterfacesMessage = (AvailableInterfacesMessage) message;
            String connectionInterface = availableInterfacesMessage.getConnectionInterface()
                    .flatMap(Arrays::stream)
                    .filter(StringUtils::isNotEmpty)
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            droneCommander.sendCommand(new ConnectCommand(connectionInterface));
        } else if (message instanceof DroneConnectionStatusMessage) {
            DroneConnectionStatusMessage connectionStatusMessage = (DroneConnectionStatusMessage) message;
            switch (connectionStatusMessage.getStatus()) {
                case Connected:
                    connectedCallback.run();
                    break;
                case Disconnected:
                    disconnectedCallback.run();
                    break;
            }
        } else {
            throw new RuntimeException("Unhandled message of type " + message.getTypeClass()
                    .getSimpleName() + " from drone!");
        }
    }

    public void sendCommandToDrone(BaseCommand command) {
        droneCommander.sendCommand(command);
    }

    private Process spawnPythonScript() throws IOException {
        ProcessBuilder procBuilder = new ProcessBuilder("/usr/local/bin/python3", "main.py");
        procBuilder.environment().put("PYTHONUNBUFFERED", "1");
        procBuilder.directory(new File("../PythonInterface/"));
        Process process = procBuilder.start();
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        return process;
    }
}
