package io.joshking.dronegestures.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.joshking.dronegestures.drone.commands.BaseCommand;
import io.joshking.dronegestures.drone.commands.BaseCommandTypeAdapter;
import io.joshking.dronegestures.drone.messages.BaseMessage;
import io.joshking.dronegestures.drone.messages.BaseMessageTypeAdapter;

public class GsonUtils {
    public static Gson GSON;

    static {
        GSON = new GsonBuilder().registerTypeHierarchyAdapter(BaseMessage.class,
                                                              new BaseMessageTypeAdapter())
                .registerTypeHierarchyAdapter(BaseCommand.class, new BaseCommandTypeAdapter())
                .create();
    }
}
