package anightdazingzoroark.riftlib.util.json;

import anightdazingzoroark.riftlib.file.HitboxDefinitionList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.JsonException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonHitboxUtils {
    public static HitboxDefinitionList.HitboxDefinition createDefinitionFromJson(JsonObject jsonObject) throws JsonException {
        return new HitboxDefinitionList.HitboxDefinition(
            locatorHitboxToHitbox(jsonObject.get("locator").getAsString()),
            jsonObject.get("width").getAsFloat(),
            jsonObject.get("height").getAsFloat(),
            jsonObject.get("damageMultiplier").getAsFloat(),
            jsonObject.get("affectedByAnim").getAsBoolean(),
            createHitboxDamageDefinitionFromJson(jsonObject.get("damageDefinitions"))
        );
    }

    public static List<HitboxDefinitionList.HitboxDamageDefinition> createHitboxDamageDefinitionFromJson(JsonElement jsonElement) {
        if (jsonElement == null) return Collections.emptyList();
        if (jsonElement.getAsJsonArray() == null) return Collections.emptyList();

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<HitboxDefinitionList.HitboxDamageDefinition> toReturn = new ArrayList<>();
        for (int x = 0; x < jsonArray.size(); x++) {
            JsonObject object = jsonArray.get(x).getAsJsonObject();
            toReturn.add(new HitboxDefinitionList.HitboxDamageDefinition(
                    object.get("damageSource") != null ? object.get("damageSource").getAsString() : null,
                    object.get("damageType") != null ? object.get("damageType").getAsString() : null,
                    object.get("damageMultiplier").getAsFloat()
            ));
        }
        return toReturn;
    }

    public static String locatorHitboxToHitbox(String locatorName) {
        if (locatorName.length() < 7 || !locatorName.substring(0, 7).equals("hitbox_")) return locatorName;
        return locatorName.substring(7);
    }

    public static boolean locatorCanBeHitbox(String locatorName) {
        return locatorName.length() >= 7 && locatorName.substring(0, 7).equals("hitbox_");
    }
}
