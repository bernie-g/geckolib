package anightdazingzoroark.riftlib.util.json;

import anightdazingzoroark.riftlib.file.HitboxDefinitionList;
import com.google.gson.JsonObject;
import net.minecraft.client.util.JsonException;

public class JsonHitboxUtils {
    public static HitboxDefinitionList.HitboxDefinition createDefinitionFromJson(JsonObject jsonObject) throws JsonException {
        return new HitboxDefinitionList.HitboxDefinition(
            locatorHitboxToHitbox(jsonObject.get("locator").getAsString()),
            jsonObject.get("width").getAsFloat(),
            jsonObject.get("height").getAsFloat(),
            jsonObject.get("damageMultiplier").getAsFloat(),
            jsonObject.get("affectedByAnim").getAsBoolean()
        );
    }

    public static String locatorHitboxToHitbox(String locatorName) {
        if (locatorName.length() < 7 || !locatorName.substring(0, 7).equals("hitbox_")) return locatorName;
        return locatorName.substring(7);
    }
}
