package software.bernie.geckolib3q.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public interface GeoArmorRendererFactory<T extends Entity> {
	ItemRenderer create(GeoArmorRendererFactory.Context ctx);

	@Environment(EnvType.CLIENT)
	public static class Context {
		private final EntityRenderDispatcher renderDispatcher;
		private final ItemRenderer itemRenderer;
		private final ResourceManager resourceManager;
		private final EntityModelSet modelLoader;
		private final Font textRenderer;

		public Context(EntityRenderDispatcher renderDispatcher, ItemRenderer itemRenderer,
				ResourceManager resourceManager, EntityModelSet modelLoader, Font textRenderer) {
			this.renderDispatcher = renderDispatcher;
			this.itemRenderer = itemRenderer;
			this.resourceManager = resourceManager;
			this.modelLoader = modelLoader;
			this.textRenderer = textRenderer;
		}

		public EntityRenderDispatcher getRenderDispatcher() {
			return this.renderDispatcher;
		}

		public ItemRenderer getItemRenderer() {
			return this.itemRenderer;
		}

		public ResourceManager getResourceManager() {
			return this.resourceManager;
		}

		public EntityModelSet getModelLoader() {
			return this.modelLoader;
		}

		public ModelPart getPart(ModelLayerLocation layer) {
			return this.modelLoader.bakeLayer(layer);
		}

		public Font getTextRenderer() {
			return this.textRenderer;
		}
	}
}
