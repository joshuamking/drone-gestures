package io.joshking.dronegestures.drone.connection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import io.joshking.dronegestures.drone.messages.BaseMessage;
import io.joshking.dronegestures.utils.GsonUtils;

public class DroneListener extends ThreadLoop {
    private final BufferedReader        errorReader;
    private final BufferedReader        reader;
    private       Logger                logger              = LoggerFactory.getLogger(DroneListener.class);
    private       Logger                msgFromDroneLogger  = LoggerFactory.getLogger(
            "MSG FROM DRONE");
    private       Consumer<BaseMessage> msgReceivedListener;
    private       Logger                pythonMessageLogger = LoggerFactory.getLogger("PYTHON MSG");

    DroneListener(Consumer<BaseMessage> msgReceivedListener, InputStream inputStream, InputStream errorStream) {
        super(DroneListener.class.getSimpleName());

        this.msgReceivedListener = msgReceivedListener;

        reader = new BufferedReader(new InputStreamReader(inputStream));
        errorReader = new BufferedReader(new InputStreamReader(errorStream));
    }

    @Override
    public void close() {
    }

    @Override
    public void loop() throws IOException {
        if (errorReader.ready()) {
            String line = reader.readLine();
            if (line != null) {
                pythonMessageLogger.error(line);
            }
        } else if (reader.ready()) {
            String line = StringUtils.trim(reader.readLine());
            if (!line.startsWith(DroneConnection.DRONE_MSG_PREFIX)) {
                pythonMessageLogger.info(line);
                return;
            } else {
                line = line.substring(DroneConnection.DRONE_MSG_PREFIX.length());
                msgFromDroneLogger.trace(line);
                msgReceivedListener.accept(GsonUtils.GSON.fromJson(line, BaseMessage.class));
            }
        }
    }

    public void waitForString(String s) {
        if (s == null || StringUtils.isEmpty(s)) {
            return;
        }

        s = StringUtils.trim(s);

        while (true) {
            try {
                if (reader.ready() && s.equals(StringUtils.trim(reader.readLine()))) {
                    return;
                } else {
                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
