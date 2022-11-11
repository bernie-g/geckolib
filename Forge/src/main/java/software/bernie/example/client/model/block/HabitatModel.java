// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLibMod
// Paste this class into your mod and follow the documentation for GeckoLibMod to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.model.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.entity.HabitatBlockEntity;
import software.bernie.example.client.renderer.block.HabitatBlockRenderer;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedBlockGeoModel;
import software.bernie.geckolib3.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link HabitatBlockEntity}
 * @see HabitatBlockEntity
 * @see HabitatBlockRenderer
 */
public class HabitatModel extends DefaultedBlockGeoModel<HabitatBlockEntity> {
	public HabitatModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "gecko_habitat"));
	}

	@Override
	public RenderType getRenderType(HabitatBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}