package software.bernie.geckolib3.renderers.geo;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.GeckoLibUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GeoItemRenderer<T extends Item & IAnimatable> implements IGeoRenderer<T>, BuiltinItemRendererRegistry.DynamicItemRenderer {
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
	public void render(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrixStackIn,
			VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
		this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack);
	}

	public void render(T animatable, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn,
			ItemStack itemStack) {
		this.currentItemStack = itemStack;
		AnimationEvent<T> itemEvent = new AnimationEvent<>(animatable, 0, 0,
				MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(itemStack));
		modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
		stack.push();
		// stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(animatable));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderLayer renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.pop();
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return GeckoLibUtil.getIDFromStack(currentItemStack);
	}
}
