package nl.lijstr.security.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * A {@link Gson} (de-)serializer that converts {@link LocalDateTime}'s into longs to limit transfer data.
 * This does however trade a bit of accuracy (nanoseconds).
 */
public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime dateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateTime.toEpochSecond(ZoneOffset.UTC));
    }

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDateTime.ofEpochSecond(jsonElement.getAsLong(), 0, ZoneOffset.UTC);
    }
}
