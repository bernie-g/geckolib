package software.bernie.geckolib3q.renderers.geo;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.model.AnimatedGeoModel;
import software.bernie.geckolib3q.util.GeckoLibUtil;

public class GeoItemRenderer<T extends Item & IAnimatable>
		implements IGeoRenderer<T>, BuiltinItemRendererRegistry.DynamicItemRenderer {
	private static final Map<Class<? extends Item>, GeoItemRenderer> renderers = new ConcurrentHashMap<>();

	// Register a model fetcher for this renderer
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof Item) {
				GeoItemRenderer renderer = renderers.get(object.getClass());
				return renderer == null ? null : renderer.getGeoModelProvider();
			}
			return null;
		});
	}

	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;

	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	public static void registerItemRenderer(Item item, GeoItemRenderer renderer) {
		renderers.put(item.getClass(), renderer);
		BuiltinItemRendererRegistry.INSTANCE.register(item, renderer);
	}

	public static GeoItemRenderer getRenderer(Class<? extends Item> item) {
		return renderers.get(item);
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return modelProvider;
	}

	@Override
	public void render(ItemStack itemStack, ItemTransforms.TransformType mode, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack);
	}

	public void render(T animatable, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn,
			ItemStack itemStack) {
		this.currentItemStack = itemStack;
		AnimationEvent<T> itemEvent = new AnimationEvent<>(animatable, 0, 0, Minecraft.getInstance().getFrameTime(),
				false, Collections.singletonList(itemStack));
		modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
		stack.pushPose();
		// stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		return this.modelProvider.getTextureResource(entity);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return GeckoLibUtil.getIDFromStack(currentItemStack);
	}
	
	protected MultiBufferSource rtb = null;

	@Override
	public void setCurrentRTB(MultiBufferSource rtb) {
		this.rtb = rtb;
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return this.rtb;
	}
}
