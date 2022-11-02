package software.bernie.geckolib3q.renderers.geo;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3q.geo.render.built.GeoBone;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.model.AnimatedGeoModel;
import software.bernie.geckolib3q.model.provider.GeoModelProvider;
import software.bernie.geckolib3q.model.provider.data.EntityModelData;
import software.bernie.geckolib3q.util.AnimationUtils;
import software.bernie.geckolib3q.util.EModelRenderCycle;
import software.bernie.geckolib3q.util.IRenderCycle;
import software.bernie.geckolib3q.util.RenderUtils;

public class GeoProjectilesRenderer<T extends Entity & IAnimatable> extends EntityRenderer<T>
		implements IGeoRenderer<T> {

	static {
		AnimationController.addModelFetcher(animatable -> animatable instanceof Entity entity ?
				(IAnimatableModel<Object>)AnimationUtils.getGeoModelForEntity(entity) : null);
	}

	protected final AnimatedGeoModel<T> modelProvider;
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;
	protected MultiBufferSource rtb = null;

	public GeoProjectilesRenderer(EntityRendererProvider.Context ctx, AnimatedGeoModel<T> modelProvider) {
		super(ctx);
		
		this.modelProvider = modelProvider;
	}

	@AvailableSince(value = "3.0.29")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.29")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn) {
		return OverlayTexture.pack(OverlayTexture.u(uIn), livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0);
	}

	@Override
	public void render(T animatable, float yaw, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight) {
		GeoModel model = this.modelProvider.getModel(modelProvider.getModelResource(animatable));
		this.dispatchedMat = poseStack.last().pose().copy();

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.pushPose();
		poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));

		AnimationEvent<T> predicate = new AnimationEvent<T>(animatable, 0, 0, partialTick,
				false, Collections.singletonList(new EntityModelData()));

		modelProvider.setLivingAnimations(animatable, getInstanceId(animatable), predicate); // TODO change to setCustomAnimations in 1.20+
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
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.animatable = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer,
				packedLight, packedOverlay, red, green, blue, alpha);
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

			Matrix4f worldState = localMatrix.copy();

			worldState.translate(new Vector3f(this.animatable.position()));
			bone.setWorldSpaceXform(worldState);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	// TODO 1.20+ change to instance method with T argument instead of entity
	public static int getPackedOverlay(Entity entity, float uIn) {
		return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.0.29")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.29")
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