package software.bernie.geckolib.util;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JSON helper class for various .json functions
 */
public final class JsonUtil {
    /**
     * Parse a Vec3 instance from a raw .json input
     */
    public static @Nullable Vec3 jsonToVec3(@Nullable JsonElement element) {
        if (element == null)
            return null;

        if (element.isJsonArray())
            return arrayToVec(jsonArrayToDoubleArray(element.getAsJsonArray()));

        if (element.isJsonObject()) {
            final JsonObject object = element.getAsJsonObject();

            if (!object.has("x") || !object.has("y") || !object.has("z"))
                throw new IllegalStateException("Json object input must have x, y, and z properties to parse into a Vec3: " + element);

            return new Vec3(GsonHelper.getAsDouble(object, "x"), GsonHelper.getAsDouble(object, "y"), GsonHelper.getAsDouble(object, "z"));
        }

        throw new IllegalStateException("Json input must be an array or object to parse into a Vec3: " + element);
    }

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
    @SuppressWarnings("unchecked")
    public static <T> T[] jsonArrayToObjectArray(@Nullable JsonArray array, JsonDeserializationContext context, Class<T> objectClass) {
        if (array == null)
            return (T[])new Object[0];

        T[] objArray = (T[])Array.newInstance(objectClass, array.size());

        for (int i = 0; i < array.size(); i++) {
            objArray[i] = context.deserialize(array.get(i), objectClass);
        }

        return objArray;
    }

    /**
     * Converts a {@link JsonArray} to an array of objects, mapped using the mapping function
     *
     * @param array The array containing the objects to be converted
     * @param mappingFunction The function to map a json element to an object of the intended type
     */
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T @Nullable[] jsonArrayToObjectArray(@Nullable JsonArray array, Function<JsonElement, @Nullable T> mappingFunction) {
        if (array == null)
            return null;

        T[] objArray = (T[])new Object[array.size()];

        for (int i = 0; i < array.size(); i++) {
            T object = mappingFunction.apply(array.get(i));

            if (object != null)
                objArray[i] = object;
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
    public static @Nullable Long getOptionalLong(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsLong(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Boolean from the provided {@link JsonObject}, or null if the element isn't present
     */
    public static @Nullable Boolean getOptionalBoolean(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsBoolean(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Float from the provided {@link JsonObject}, or null if the element isn't present
     */
    public static @Nullable Float getOptionalFloat(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsFloat(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Double from the provided {@link JsonObject}, or null if the element isn't present
     */
    public static @Nullable Double getOptionalDouble(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsDouble(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Integer from the provided {@link JsonObject}, or null if the element isn't present
     */
    public static @Nullable Integer getOptionalInteger(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsInt(obj, elementName) : null;
    }

    /**
     * Convert a double array to a positional Vec3
     */
    public static Vec3 arrayToVec(double[] array) {
        if (array[0] == 0 && array[1] == 0 && array[2] == 0)
            return Vec3.ZERO;

        return new Vec3(array[0], array[1], array[2]);
    }

    private JsonUtil() {}
}
