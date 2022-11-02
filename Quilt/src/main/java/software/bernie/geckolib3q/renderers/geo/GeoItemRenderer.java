package software.bernie.geckolib3q.renderers.geo;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3q.geo.render.built.GeoBone;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.model.AnimatedGeoModel;
import software.bernie.geckolib3q.util.EModelRenderCycle;
import software.bernie.geckolib3q.util.GeckoLibUtil;
import software.bernie.geckolib3q.util.IRenderCycle;
import software.bernie.geckolib3q.util.RenderUtils;

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
	protected MultiBufferSource rtb = null;

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
	public void render(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (transformType == ItemTransforms.TransformType.GUI) {
			poseStack.pushPose();
			MultiBufferSource.BufferSource defaultBufferSource = bufferSource instanceof MultiBufferSource.BufferSource bufferSource2 ?
					bufferSource2 : Minecraft.getInstance().renderBuffers().bufferSource();
			Lighting.setupForFlatItems();
			render((T)stack.getItem(), poseStack, bufferSource, packedLight, stack);
			defaultBufferSource.endBatch();
			RenderSystem.enableDepthTest();
			Lighting.setupFor3DItems();
			poseStack.popPose();
		}
		else {
			this.render((T)stack.getItem(), poseStack, bufferSource, packedLight, stack);
		}
	}

	public void render(T animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
			ItemStack stack) {
		this.currentItemStack = stack;
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelResource(animatable));
		AnimationEvent animationEvent = new AnimationEvent(animatable, 0, 0, Minecraft.getInstance().getFrameTime(), false, Collections.singletonList(stack));
		this.dispatchedMat = poseStack.last().pose().copy();

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.modelProvider.setLivingAnimations(animatable, getInstanceId(animatable), animationEvent); // TODO change to setCustomAnimations in 1.20+
		poseStack.pushPose();
		poseStack.translate(0.5f, 0.51f, 0.5f);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, poseStack, bufferSource, null, packedLight);
		RenderType renderType = getRenderType(animatable, 0, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.NO_OVERLAY,
				renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
				renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
		poseStack.popPose();
	}

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
							VertexConsumer buffer, int packedLight, int packedOverlayIn, float red, float green, float blue,
							float alpha) {
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.animatable = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.translate(new Vector3f(getRenderOffset(this.animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	public Vec3 getRenderOffset(T animatable, float partialTick) {
		return Vec3.ZERO;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		return this.modelProvider.getTextureResource(entity);
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
	public void setCurrentRTB(MultiBufferSource bufferSource) {
		this.rtb = bufferSource;
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return this.rtb;
	}
}
