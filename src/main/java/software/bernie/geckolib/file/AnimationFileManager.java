/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * An animation file manager is responsible for reading animation json files.
 */
public class AnimationFileManager
{
	private Identifier location;

	/**
	 * Instantiates a new Animation file manager.
	 *
	 * @param Location the resource location of the json file
	 */
	public AnimationFileManager(Identifier Location)
	{
		location = Location;
	}

	private JsonObject loadAnimationFile(Identifier location) throws Exception
	{
		Gson GSON = new Gson();
		ReloadableResourceManager resourceManager = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();
		ResourceImpl resource = (ResourceImpl) resourceManager.getResource(location);
		Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
		JsonObject jsonobject = JsonHelper.deserialize(GSON, reader, JsonObject.class);
		resource.close();
		return jsonobject;
	}

	/**
	 * Loads json from the current animation file.
	 *
	 * @return the json object
	 * @throws Exception Thrown if an exception is encountered while loading the file
	 */
	public JsonObject loadAnimationFile() throws Exception
	{
		return loadAnimationFile(location);
	}
}
