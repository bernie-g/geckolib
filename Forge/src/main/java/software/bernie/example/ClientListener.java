package software.bernie.example;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.example.client.renderer.armor.GeckoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.CarGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleExtendedRendererEntityRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.LERenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.entity.TexturePerBoneTestEntityRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.client.renderer.tile.HabitatTileRenderer;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientListener {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(final FMLClientSetupEvent event) {
		if (GeckoLibMod.shouldRegisterExamples()) {
			RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.GEO_EXAMPLE_ENTITY.get(),
					ExampleGeoRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BIKE_ENTITY.get(), BikeGeoRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.CAR_ENTITY.get(), CarGeoRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.GEOLAYERENTITY.get(), LERenderer::new);

			RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.EXTENDED_RENDERER_EXAMPLE.get(),
					ExampleExtendedRendererEntityRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE.get(),
					TexturePerBoneTestEntityRenderer::new);

			GeoArmorRenderer.registerArmorRenderer(GeckoArmorItem.class, () -> new GeckoArmorRenderer());
			ClientRegistry.bindTileEntityRenderer(TileRegistry.HABITAT_TILE.get(), HabitatTileRenderer::new);
			ClientRegistry.bindTileEntityRenderer(TileRegistry.FERTILIZER.get(), FertilizerTileRenderer::new);

			RenderTypeLookup.setRenderLayer(BlockRegistry.HABITAT_BLOCK.get(), RenderType.cutout());

			EntityRendererManager renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
			ReplacedCreeperRenderer creeperRenderer = new ReplacedCreeperRenderer(renderManager);
			renderManager.renderers.replace(EntityType.CREEPER, creeperRenderer);
			GeoReplacedEntityRenderer.registerReplacedEntity(ReplacedCreeperEntity.class, creeperRenderer);
		}
	}
}
