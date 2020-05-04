package software.bernie.geckolib.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AnimationFileManager
{
	private ResourceLocation location;
	public AnimationFileManager(ResourceLocation Location)
	{
		location = Location;
	}

	private JsonObject loadAnimationFile(ResourceLocation location) throws Exception
	{
		Gson GSON = new Gson();
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		SimpleResource resource = (SimpleResource) resourceManager.getResource(location);
		Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
		JsonObject jsonobject = JSONUtils.fromJson(GSON, reader, JsonObject.class);
		resource.close();
		return jsonobject;
	}

	public JsonObject loadAnimationFile() throws Exception
	{
		return loadAnimationFile(location);
	}
}
