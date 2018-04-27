package io.joshking.dronegestures.drone.commands;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BaseCommandTypeAdapter implements JsonDeserializer<BaseCommand>, JsonSerializer<BaseCommand> {

    @SuppressWarnings("WeakerAccess") public static final String                                    TYPE_KEY = "type";
    private final                                         Map<String, Class<? extends BaseCommand>> baseCommandSubclassesMap;
    private final                                         Gson                                      gson;

    public BaseCommandTypeAdapter() {
        gson = new Gson();
        baseCommandSubclassesMap = new HashMap<>();

        registerCommand(ConnectCommand.class);
        registerCommand(MoveCommand.class);
    }

    @Override
    public BaseCommand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String                       type  = json.getAsJsonObject().get(TYPE_KEY).getAsString();
        Class<? extends BaseCommand> clazz = baseCommandSubclassesMap.get(type);
        if (clazz == null) {
            throw new RuntimeException("Unrecognized command of type " + type + ". Did you forget to register this command?");
        }
        return gson.fromJson(json, clazz);
    }

    @Override
    public JsonElement serialize(BaseCommand src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = gson.toJsonTree(src).getAsJsonObject();
        jsonObject.add(TYPE_KEY, new JsonPrimitive(src.getTypeClass().getSimpleName()));
        return jsonObject;
    }

    private void registerCommand(Class<? extends BaseCommand> clazz) {
        baseCommandSubclassesMap.put(clazz.getSimpleName(), clazz);
    }
}
