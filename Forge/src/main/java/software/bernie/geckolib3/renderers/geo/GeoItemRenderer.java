package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.RenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
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

public abstract class GeoItemRenderer<T extends Item & IAnimatable> extends BlockEntityWithoutLevelRenderer
		implements IGeoRenderer<T> {
	// Register a model fetcher for this renderer
	static {
		AnimationController.addModelFetcher(animatable -> {
			if (animatable instanceof Item item
					&& RenderProperties.get(item).getItemStackRenderer()instanceof GeoItemRenderer geoItemRenderer)
				return (IAnimatableModel<Object>) geoItemRenderer.getGeoModelProvider();

			return null;
		});
	}

	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	protected MultiBufferSource rtb = null;

	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
		this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(),
				modelProvider);
	}

	public GeoItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet,
			AnimatedGeoModel<T> modelProvider) {
		super(dispatcher, modelSet);

		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return modelProvider;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	// fixes the item lighting
	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (transformType == ItemTransforms.TransformType.GUI) {
			poseStack.pushPose();
			MultiBufferSource.BufferSource defaultBufferSource = bufferSource instanceof MultiBufferSource.BufferSource bufferSource2
					? bufferSource2
					: Minecraft.getInstance().renderBuffers().bufferSource();
			Lighting.setupForFlatItems();
			render((T) stack.getItem(), poseStack, bufferSource, packedLight, stack);
			defaultBufferSource.endBatch();
			RenderSystem.enableDepthTest();
			Lighting.setupFor3DItems();
			poseStack.popPose();
		} else {
			this.render((T) stack.getItem(), poseStack, bufferSource, packedLight, stack);
		}
	}

	public void render(T animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
			ItemStack stack) {
		this.currentItemStack = stack;
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(animatable));
		AnimationEvent animationEvent = new AnimationEvent(animatable, 0, 0, Minecraft.getInstance().getFrameTime(),
				false, Collections.singletonList(stack));
		this.dispatchedMat = poseStack.last().pose().copy();

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.modelProvider.setLivingAnimations(animatable, getInstanceId(animatable), animationEvent); // TODO change to
																										// setCustomAnimations
																										// in 1.20+
		poseStack.pushPose();
		poseStack.translate(0.5f, 0.51f, 0.5f);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, poseStack, bufferSource, null, packedLight);
		RenderType renderType = getRenderType(animatable, 0, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.NO_OVERLAY,
				renderColor.getRed() / 255f, renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
				renderColor.getAlpha() / 255f);
		poseStack.popPose();
	}

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
			VertexConsumer buffer, int packedLight, int packedOverlayIn, float red, float green, float blue,
			float alpha) {
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.animatable = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlayIn, red, green, blue, alpha);
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
	public ResourceLocation getTextureLocation(T animatable) {
		return this.modelProvider.getTextureLocation(animatable);
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
		return GeckoLibUtil.getIDFromStack(this.currentItemStack);
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
