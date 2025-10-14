package software.bernie.geckolib.util;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Json helper class for various json functions
 */
public final class JsonUtil {
    private JsonUtil() {}

    /**
     * Convert a {@link JsonArray} of doubles to a {@code double[]}
     * <p>
     * No type checking is done, so if the array contains anything other than doubles, this will throw an exception
     * <p>
     * Ensures a minimum size of 3, as this is the expected usage of this method
     */
    public static double[] jsonArrayToDoubleArray(@Nullable JsonArray array) throws JsonParseException{
        if (array == null)
            return new double[3];

        double[] output = new double[array.size()];

        for (int i = 0; i < array.size(); i++) {
            output[i] = array.get(i).getAsDouble();
        }

        return output;
    }

    /**
     * Converts a {@link JsonArray} of a given object type to an array of that object, deserialized from their respective {@link JsonElement JsonElements}
     *
     * @param array The array containing the objects to be converted
     * @param context The {@link com.google.gson.Gson} context for deserialization
     * @param objectClass The object type that the array contains
     */
    public static <T> T[] jsonArrayToObjectArray(JsonArray array, JsonDeserializationContext context, Class<T> objectClass) {
        T[] objArray = (T[]) Array.newInstance(objectClass, array.size());

        for (int i = 0; i < array.size(); i++) {
            objArray[i] = context.deserialize(array.get(i), objectClass);
        }

        return objArray;
    }

    /**
     * Converts a {@link JsonArray} to a {@link List} of elements of a pre-determined type
     *
     * @param array The {@code JsonArray} to convert
     * @param elementTransformer Transformation function that converts a {@link JsonElement} to the intended output object
     */
    public static <T> List<T> jsonArrayToList(@Nullable JsonArray array, Function<JsonElement, T> elementTransformer) {
        if (array == null)
            return new ObjectArrayList<>();

        List<T> list = new ObjectArrayList<>(array.size());

        for (JsonElement element : array) {
            list.add(elementTransformer.apply(element));
        }

        return list;
    }

    /**
     * Converts a {@link JsonObject} to a {@link Map} of String keys to their respective objects
     *
     * @param obj The base {@code JsonObject} to convert
     * @param context The {@link Gson} deserialization context
     * @param objectType The object class that the map should contain
     */
    public static <T> Map<String, T> jsonObjToMap(JsonObject obj, JsonDeserializationContext context, Class<T> objectType) {
        Map<String, T> map = new Object2ObjectOpenHashMap<>(obj.size());

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            map.put(entry.getKey(), context.deserialize(entry.getValue(), objectType));
        }

        return map;
    }

    /**
     * Retrieves an optionally present Long from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Long getOptionalLong(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsLong(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Boolean from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Boolean getOptionalBoolean(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsBoolean(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Float from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Float getOptionalFloat(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsFloat(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Double from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Double getOptionalDouble(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsDouble(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Integer from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Integer getOptionalInteger(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsInt(obj, elementName) : null;
    }

    /**
     * Converts a given double array to its {@link Vec3} equivalent
     */
    public static Vec3 arrayToVec(double[] array) {
        return new Vec3(array[0], array[1], array[2]);
    }
}
