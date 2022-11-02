package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
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
	protected VertexConsumerProvider rtb = null;

	@AvailableSince(value = "3.0.65")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	public GeoProjectilesRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<T> modelProvider) {
		super(ctx);
		
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(T animatable, float yaw, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, int packedLight) {
		GeoModel model = this.modelProvider.getModel(modelProvider.getModelLocation(animatable));
		this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.push();
		poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(partialTick, animatable.prevYaw, animatable.getYaw()) - 90));
		poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(partialTick, animatable.prevPitch, animatable.getPitch())));

		AnimationEvent<T> predicate = new AnimationEvent<T>(animatable, 0, 0, partialTick,
				false, Collections.singletonList(new EntityModelData()));

		modelProvider.setLivingAnimations(animatable, getInstanceId(animatable), predicate); // TODO change to setCustomAnimations in 1.20+
		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

		Color renderColor = getRenderColor(animatable, partialTick, poseStack, bufferSource, null, packedLight);
		RenderLayer renderType = getRenderType(animatable, partialTick, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));

		if (!animatable.isInvisibleTo(MinecraftClient.getInstance().player)) {
			render(model, animatable, partialTick, renderType, poseStack, bufferSource, null, packedLight,
					getPackedOverlay(animatable, 0), renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
					renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
		}

		poseStack.pop();
		super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderEarly(T animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource,
			VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
			float alpha) {
		this.renderEarlyMat = poseStack.peek().getPositionMatrix().copy();
		this.animatable = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer,
				packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.peek().getPositionMatrix().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.addToLastColumn(new Vec3f(getPositionOffset(this.animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.addToLastColumn(new Vec3f(this.animatable.getPos()));
			bone.setWorldSpaceXform(worldState);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	public void multiplyBackward(Matrix4f first, Matrix4f other) {
		Matrix4f copy = other.copy();
		copy.multiply(first);
		first.load(copy);
	}

	// TODO 1.20+ change to instance method with T argument instead of entity
	public static int getPackedOverlay(Entity entity, float uIn) {
		return OverlayTexture.getUv(OverlayTexture.getU(uIn), false);
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getHeightScale(T animatable) {
		return this.heightScale;
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	@Override
	public Identifier getTexture(T entity) {
		return getTextureLocation(entity);
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
		return animatable.getUuid().hashCode();
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