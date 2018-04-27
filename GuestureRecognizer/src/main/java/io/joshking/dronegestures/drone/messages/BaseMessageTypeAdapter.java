package io.joshking.dronegestures.drone.messages;

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

public class BaseMessageTypeAdapter implements JsonDeserializer<BaseMessage>, JsonSerializer<BaseMessage> {

    @SuppressWarnings("WeakerAccess") public static final String                                    TYPE_KEY = "type";
    private final                                         Map<String, Class<? extends BaseMessage>> baseMessageSubclassesMap;
    private final                                         Gson                                      gson;

    public BaseMessageTypeAdapter() {
        gson = new Gson();
        baseMessageSubclassesMap = new HashMap<>();

        registerCommand(AvailableInterfacesMessage.class);
        registerCommand(DroneConnectionStatusMessage.class);
        registerCommand(EchoMessage.class);
    }

    @Override
    public BaseMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String                       type  = json.getAsJsonObject().get(TYPE_KEY).getAsString();
        Class<? extends BaseMessage> clazz = baseMessageSubclassesMap.get(type);
        if (clazz == null) {
            throw new RuntimeException("Unrecognized message of type " + type + ". Did you forget to register this message?");
        }
        return gson.fromJson(json, clazz);
    }

    @Override
    public JsonElement serialize(BaseMessage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = gson.toJsonTree(src).getAsJsonObject();
        jsonObject.add(TYPE_KEY, new JsonPrimitive(src.getTypeClass().getSimpleName()));
        return jsonObject;
    }

    private void registerCommand(Class<? extends BaseMessage> clazz) {
        baseMessageSubclassesMap.put(clazz.getSimpleName(), clazz);
    }
}
