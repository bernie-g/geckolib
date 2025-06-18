package anightdazingzoroark.riftlib.file;

import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.util.json.JsonHitboxUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

public class HitboxLoader {
    public HitboxDefinitionList loadHitboxes(IResourceManager resourceManager, ResourceLocation location) {
        JsonObject jsonRepresentation = loadFile(location, resourceManager);
        HitboxDefinitionList hitboxList = new HitboxDefinitionList();

        try {
            JsonArray jsonArray = jsonRepresentation.getAsJsonArray("hitboxes");
            for (int x = 0; x < jsonArray.size(); x++) {
                JsonObject object = jsonArray.get(x).getAsJsonObject();
                HitboxDefinitionList.HitboxDefinition hitboxDefinition = JsonHitboxUtils.createDefinitionFromJson(object);
                hitboxList.list.add(hitboxDefinition);
            }
        }
        catch (Exception e) {
            RiftLib.LOGGER.error(String.format("Error parsing %S", location), e);
            throw (new RuntimeException(e));
        }
        System.out.println(hitboxList.list);
        return hitboxList;
    }

    private JsonObject loadFile(ResourceLocation location, IResourceManager manager) {
        String content = getResourceAsString(location, manager);
        Gson GSON = new Gson();
        return JsonUtils.fromJson(GSON, new StringReader(content), JsonObject.class);
    }

    public static String getResourceAsString(ResourceLocation location, IResourceManager manager) {
        try (InputStream inputStream = manager.getResource(location).getInputStream()) {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (Exception e) {
            String message = "Couldn't load " + location;
            RiftLib.LOGGER.error(message, e);
            throw new RuntimeException(new FileNotFoundException(location.toString()));
        }
    }
}
