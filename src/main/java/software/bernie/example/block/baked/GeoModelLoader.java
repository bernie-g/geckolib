package software.bernie.example.block.baked;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeoModelLoader implements IModelLoader<GeoModelGeometry>
{
	public Map<ResourceLocation, Pair<ResourceLocation, ResourceLocation>> models = new HashMap<ResourceLocation, Pair<ResourceLocation, ResourceLocation>>();
	public Set<ResourceLocation> textures = new HashSet<ResourceLocation>();

	public GeoModelLoader register(ResourceLocation blockModel, ResourceLocation geoModel, ResourceLocation texture)
	{
		this.models.put(blockModel, Pair.of(geoModel, texture));
		this.textures.add(texture);

		return this;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{}

	@Override
	public GeoModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
	{
		return new GeoModelGeometry(this);
	}
}