package software.bernie.geckolib.file;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.geo.exception.GeoModelException;
import software.bernie.geckolib.geo.raw.pojo.Converter;
import software.bernie.geckolib.geo.raw.pojo.FormatVersion;
import software.bernie.geckolib.geo.raw.pojo.RawGeoModel;
import software.bernie.geckolib.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib.geo.render.GeoBuilder;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.IAnimatableModel;
import software.bernie.geckolib.model.IGeoModel;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GeoModelLoader
{
	private final IGeoModel provider;
	private RawGeoModel rawModel;
	private RawGeometryTree rawGeometryTree;
	private GeoModel model;

	public GeoModelLoader(IGeoModel provider)
	{
		this.provider = provider;
	}

	public void loadModel(IResourceManager resourceManager)
	{
		try
		{
			//Deserialize from json into basic json objects, bones are still stored as a flat list
			this.rawModel = Converter.fromJsonString(getModelAsString(resourceManager));
			if (rawModel.getFormatVersion() != FormatVersion.VERSION_1_12_0)
			{
				throw new GeoModelException(provider.getModelLocation(), "Wrong geometry json version, expected 1.12.0");
			}

			//Parse the flat list of bones into a raw hierarchical tree of "BoneGroup"s
			this.rawGeometryTree = RawGeometryTree.parseHierarchy(rawModel);

			//Build the quads and cubes from the raw tree into a built and ready to be rendered GeoModel
			this.model = GeoBuilder.constructGeoModel(rawGeometryTree);
		}
		catch (Exception e)
		{
			GeckoLib.LOGGER.error(String.format("Error parsing %S", provider.getModelLocation()), e);
			throw (new RuntimeException(e));
		}
	}

	private String getModelAsString(IResourceManager resourceManager)
	{
		try (InputStream inputStream = getStreamForResourceLocation(provider.getModelLocation()))
		{
			return IOUtils.toString(inputStream);
		}
		catch (Exception e)
		{
			String message = "Couldn't load " + provider.getModelLocation();
			GeckoLib.LOGGER.error(message, e);
			throw new RuntimeException(new FileNotFoundException(provider.getModelLocation().toString()));
		}
	}

	public InputStream getStreamForResourceLocation(ResourceLocation resourceLocation)
	{
		return new BufferedInputStream(GeckoLib.class.getResourceAsStream("/assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath()));
	}

	public RawGeoModel getRawModel()
	{
		return rawModel;
	}

	public RawGeometryTree getRawGeometryTree()
	{
		return rawGeometryTree;
	}

	public GeoModel getModel()
	{
		return model;
	}
}
