package software.bernie.geckolib.file;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.JSONException;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.JSONUtils;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.util.json.JsonAnimationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnimationFileLoader
{
	private IFileProvider provider;

	public AnimationFileLoader(IFileProvider provider)
	{
		this.provider = provider;
	}

	/**
	 * If animations should loop by default and ignore their pre-existing loop settings (that you can enable in blockbench by right clicking)
	 */
	protected boolean loopByDefault = false;
	private JsonObject animationFile;

	private HashMap<String, Animation> animationList = new HashMap();

	public HashMap<String, Animation> getAnimationList()
	{
		return animationList;
	}

	public void setAnimationList(HashMap<String, Animation> animationList)
	{
		this.animationList = animationList;
	}

	private void loadAllAnimations(MolangParser parser)
	{
		animationList.clear();
		Set<Map.Entry<String, JsonElement>> entrySet = JsonAnimationUtils.getAnimations(getAnimationFile());
		for (Map.Entry<String, JsonElement> entry : entrySet)
		{
			String animationName = entry.getKey();
			Animation animation = null;
			try
			{
				animation = JsonAnimationUtils.deserializeJsonToAnimation(JsonAnimationUtils.getAnimation(getAnimationFile(), animationName), parser);
				if (loopByDefault)
				{
					animation.loop = true;
				}
			}
			catch (JSONException e)
			{
				GeckoLib.LOGGER.error("Could not load animation: " + animationName, e);
				throw new RuntimeException(e);
			}
			animationList.put(animationName, animation);
		}
	}

	public boolean isLoopByDefault()
	{
		return loopByDefault;
	}

	public void setLoopByDefault(boolean loopByDefault)
	{
		this.loopByDefault = loopByDefault;
	}

	public Animation getAnimation(String name)
	{
		return animationList.get(name);
	}

	/**
	 * Internal method for handling reloads of animation files. Do not override.
	 */
	public void onResourceManagerReload(IResourceManager resourceManager, MolangParser parser)
	{
		try
		{
			Gson GSON = new Gson();
			SimpleResource resource = (SimpleResource) resourceManager.getResource(provider.getAnimationFileLocation());
			InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
			Reader reader = new BufferedReader(
					stream);
			JsonObject jsonobject = JSONUtils.fromJson(GSON, reader, JsonObject.class);
			resource.close();
			stream.close();
			setAnimationFile(jsonobject);
			loadAllAnimations(parser);
		}
		catch (IOException e)
		{
			GeckoLib.LOGGER.error("Encountered error while loading animations.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the current animation file.
	 *
	 * @return the animation file
	 */
	public JsonObject getAnimationFile()
	{
		return animationFile;
	}

	/**
	 * Sets the animation file to read from.
	 *
	 * @param animationFile The animation file
	 */
	public void setAnimationFile(JsonObject animationFile)
	{
		this.animationFile = animationFile;
	}

	/**
	 * Gets a json animation by name.
	 *
	 * @param name The name
	 * @return the animation by name
	 * @throws JSONException
	 */
	public Map.Entry<String, JsonElement> getAnimationByName(String name) throws JSONException
	{
		return JsonAnimationUtils.getAnimation(getAnimationFile(), name);
	}
}
