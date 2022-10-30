package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.util.AnimationUtils;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

public class GeoProjectilesRenderer<T extends Entity & IAnimatable> extends EntityRenderer<T>
		implements IGeoRenderer<T> {

	static {
		AnimationController.addModelFetcher(animatable -> animatable instanceof Entity entity
				? (IAnimatableModel<Object>) AnimationUtils.getGeoModelForEntity(entity)
				: null);
	}

	private final AnimatedGeoModel<T> modelProvider;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	protected float widthScale = 1;
	protected float heightScale = 1;
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;
	protected MultiBufferSource rtb = null;

	public GeoProjectilesRenderer(EntityRendererProvider.Context ctx, AnimatedGeoModel<T> modelProvider) {
		super(ctx);

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

	@Override
	public void render(T animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
			int packedLight) {
		GeoModel model = this.modelProvider.getModel(modelProvider.getModelResource(animatable));
		this.dispatchedMat = poseStack.last().pose();

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));

		AnimationEvent<T> predicate = new AnimationEvent<T>(animatable, 0, 0, partialTick, false,
				Collections.singletonList(new EntityModelData()));

		modelProvider.setCustomAnimations(animatable, getInstanceId(animatable), predicate);
		
		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

		Color renderColor = getRenderColor(animatable, partialTick, poseStack, bufferSource, null, packedLight);
		RenderType renderType = getRenderType(animatable, partialTick, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));

		if (!animatable.isInvisibleTo(Minecraft.getInstance().player)) {
			render(model, animatable, partialTick, renderType, poseStack, bufferSource, null, packedLight,
					getPackedOverlay(animatable, 0), renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
					renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
		}

		poseStack.popPose();
		super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
			VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
			float alpha) {
		this.renderEarlyMat = poseStack.last().pose();
		this.animatable = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.translate(new Vector3f((float) getRenderOffset(animatable, 1).x(),
					(float) getRenderOffset(animatable, 1).y(), (float) getRenderOffset(animatable, 1).z()));

			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix;
			worldState.translate(
					new Vector3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
			bone.setWorldSpaceXform(worldState);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	// TODO 1.20+ change to instance method with T argument instead of entity
	public int getPackedOverlay(T entity, float uIn) {
		return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public float getHeightScale(T animatable) {
		return this.heightScale;
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
	public int getInstanceId(T animatable) {
		return animatable.getUUID().hashCode();
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