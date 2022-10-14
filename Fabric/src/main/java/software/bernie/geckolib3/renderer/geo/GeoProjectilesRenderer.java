package software.bernie.geckolib3.renderer.geo;

import java.util.Collections;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
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

public class GeoProjectilesRenderer<T extends Entity & IAnimatable> extends EntityRenderer<T>
		implements IGeoRenderer<T> {

	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof Entity) {
				return (IAnimatableModel<Object>) AnimationUtils.getGeoModelForEntity((Entity) object);
			}
			return null;
		});
	}

	protected final AnimatedGeoModel<T> modelProvider;
	protected float widthScale;
	protected float heightScale;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@AvailableSince(value = "3.0.95")
	protected IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.95")
	protected void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	public GeoProjectilesRenderer(EntityRenderDispatcher renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	@AvailableSince(value = "3.0.95")
	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.95")
	protected float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			VertexConsumerProvider bufferIn, int packedLightIn) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entityIn));
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = matrixStackIn.peek().getModel().copy();
		matrixStackIn.push();
		matrixStackIn.multiply(Vec3f.POSITIVE_Y
				.getDegreesQuaternion(MathHelper.lerp(partialTicks, entityIn.prevYaw, entityIn.yaw) - 90.0F));
		matrixStackIn.multiply(Vec3f.POSITIVE_Z
				.getDegreesQuaternion(MathHelper.lerp(partialTicks, entityIn.prevPitch, entityIn.pitch)));

		float lastLimbDistance = 0.0F;
		float limbSwing = 0.0F;
		EntityModelData entityModelData = new EntityModelData();
		AnimationEvent<T> predicate = new AnimationEvent<T>(entityIn, limbSwing, lastLimbDistance, partialTicks,
				!(lastLimbDistance > -0.15F && lastLimbDistance < 0.15F), Collections.singletonList(entityModelData));
		if (modelProvider instanceof IAnimatableModel) {
			((IAnimatableModel<T>) modelProvider).setLivingAnimations(entityIn, this.getUniqueID(entityIn), predicate);
		}
		MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entityIn));
		Color renderColor = getRenderColor(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn);
		RenderLayer renderType = getRenderType(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn,
				getTexture(entityIn));
		render(model, entityIn, partialTicks, renderType, matrixStackIn, bufferIn, null, packedLightIn,
				getPackedOverlay(entityIn, 0), (float) renderColor.getRed() / 255f,
				(float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
				(float) renderColor.getAlpha() / 255);
		matrixStackIn.pop();
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderLayer type, MatrixStack matrixStackIn,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
		IGeoRenderer.super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float partialTicks,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		renderEarlyMat = stackIn.peek().getModel().copy();
		this.animatable = animatable;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, alpha);
		if (this.getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL /* Pre-Layers */) {
			float width = this.getWidthScale(animatable);
			float height = this.getHeightScale(animatable);
			stackIn.scale(width, height, width);
		}
	}
	
	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.peek();
			Matrix4f boneMat = entry.getModel().copy();

			// Model space
			Matrix4f renderEarlyMatInvert = renderEarlyMat.copy();
			renderEarlyMatInvert.invert();
			Matrix4f modelPosBoneMat = boneMat.copy();
			multiplyBackward(modelPosBoneMat, renderEarlyMatInvert);
			bone.setModelSpaceXform(modelPosBoneMat);

			// Local space
			Matrix4f dispatchedMatInvert = this.dispatchedMat.copy();
			dispatchedMatInvert.invert();
			Matrix4f localPosBoneMat = boneMat.copy();
			multiplyBackward(localPosBoneMat, dispatchedMatInvert);
			// (Offset is the only transform we may want to preserve from the dispatched mat)
			Vec3d renderOffset = this.getPositionOffset(animatable, 1.0F);
			localPosBoneMat.addToLastColumn(new Vec3f((float) renderOffset.getX(), (float) renderOffset.getY(), (float) renderOffset.getZ()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			worldPosBoneMat.addToLastColumn(new Vec3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
			bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

    public void multiplyBackward(Matrix4f first, Matrix4f other) {
        Matrix4f copy = other.copy();
        copy.multiply(first);
        new Matrix4f(copy);
    }

	public static int getPackedOverlay(Entity livingEntityIn, float uIn) {
		return OverlayTexture.getUv(OverlayTexture.getU(uIn), false);
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	@Override
	public Identifier getTexture(T entity) {
		return getTextureLocation(entity);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getUuid().hashCode();
	}
	
	protected VertexConsumerProvider rtb = null;

	@Override
	public void setCurrentRTB(VertexConsumerProvider rtb) {
		this.rtb = rtb;
	}

	@Override
	public VertexConsumerProvider getCurrentRTB() {
		return this.rtb;
	}

}
