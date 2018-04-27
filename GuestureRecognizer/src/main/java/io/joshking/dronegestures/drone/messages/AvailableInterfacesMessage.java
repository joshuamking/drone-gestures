package io.joshking.dronegestures.drone.messages;

import java.util.Arrays;
import java.util.stream.Stream;

public class AvailableInterfacesMessage extends BaseMessage {

    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"}) private String[][] availableInterfaces;


    @Override
    public Class getTypeClass() {
        return getClass();
    }

    public Stream<String[]> getConnectionInterface() {
        return Arrays.stream(availableInterfaces);
    }
}
