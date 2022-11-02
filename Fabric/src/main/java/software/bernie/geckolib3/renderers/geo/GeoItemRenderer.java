package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

public class GeoItemRenderer<T extends Item & IAnimatable>
		implements IGeoRenderer<T>, BuiltinItemRendererRegistry.DynamicItemRenderer {
	protected static final Map<Class<? extends Item>, GeoItemRenderer> renderers = new ConcurrentHashMap<>();

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
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected VertexConsumerProvider rtb = null;

	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
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

	@AvailableSince(value = "3.1.23")
	@Override
	public float getWidthScale(T animatable2) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public float getHeightScale(T animatable) {
		return this.heightScale;
	}

	// fixes the item lighting
	@Override
	public void render(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
		if (transformType == ModelTransformation.Mode.GUI) {
			poseStack.push();
			VertexConsumerProvider.Immediate defaultBufferSource = bufferSource instanceof VertexConsumerProvider.Immediate bufferSource2 ?
					bufferSource2 : MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
			DiffuseLighting.disableGuiDepthLighting();
			render((T)stack.getItem(), poseStack, bufferSource, packedLight, stack);
			defaultBufferSource.draw();
			RenderSystem.enableDepthTest();
			DiffuseLighting.enableGuiDepthLighting();
			poseStack.pop();
		}
		else {
			this.render((T)stack.getItem(), poseStack, bufferSource, packedLight, stack);
		}
	}

	public void render(T animatable, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight,
			ItemStack stack) {
		this.currentItemStack = stack;
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(animatable));
		AnimationEvent animationEvent = new AnimationEvent(animatable, 0, 0, MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(stack));
		this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.modelProvider.setLivingAnimations(animatable, getInstanceId(animatable), animationEvent); // TODO change to setCustomAnimations in 1.20+
		poseStack.push();
		poseStack.translate(0.5f, 0.51f, 0.5f);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, poseStack, bufferSource, null, packedLight);
		RenderLayer renderType = getRenderType(animatable, 0, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.DEFAULT_UV,
				renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
				renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
		poseStack.pop();
	}

	@Override
	public void renderEarly(T animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource,
							VertexConsumer buffer, int packedLight, int packedOverlayIn, float red, float green, float blue,
							float alpha) {
		this.renderEarlyMat = poseStack.peek().getPositionMatrix().copy();
		this.animatable = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.peek().getPositionMatrix().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.addToLastColumn(new Vec3f(getRenderOffset(this.animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	public Vec3d getRenderOffset(T animatable, float partialTick) {
		return Vec3d.ZERO;
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	/**
	 * Use {@link IGeoRenderer#getInstanceId(Object)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public Integer getUniqueID(T animatable) {
		return getInstanceId(animatable);
	}

	@Override
	public int getInstanceId(T animatable) {
		return GeckoLibUtil.getIDFromStack(currentItemStack);
	}

	@Override
	public void setCurrentRTB(VertexConsumerProvider bufferSource) {
		this.rtb = bufferSource;
	}

	@Override
	public VertexConsumerProvider getCurrentRTB() {
		return this.rtb;
	}
}
