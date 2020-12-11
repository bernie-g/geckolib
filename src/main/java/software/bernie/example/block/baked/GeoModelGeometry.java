package software.bernie.example.block.baked;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class GeoModelGeometry implements IModelGeometry<GeoModelGeometry>
{
	public GeoModelLoader loader;

	public GeoModelGeometry(GeoModelLoader loader)
	{
		this.loader = loader;
	}

	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
	{
		ResourceLocation location = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
		Pair<ResourceLocation, ResourceLocation> model = this.loader.models.get(location);

		if (model != null)
		{
			return new GeoBakedModel(model.getFirst(), model.getSecond());
		}

		return null;
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
	{
		List<Material> materials = new ArrayList<Material>();

		for (ResourceLocation texture : this.loader.textures)
		{
			materials.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, texture));
		}

		return materials;
	}
}