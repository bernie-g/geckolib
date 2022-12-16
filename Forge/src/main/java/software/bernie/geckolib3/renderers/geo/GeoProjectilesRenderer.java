package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;

	public GeoProjectilesRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entityIn));
		this.dispatchedMat = matrixStackIn.last().pose().copy();
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(
				Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 90.0F));
		matrixStackIn
				.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)));

		float lastLimbDistance = 0.0F;
		float limbSwing = 0.0F;
		EntityModelData entityModelData = new EntityModelData();
		AnimationEvent<T> predicate = new AnimationEvent<T>(entityIn, limbSwing, lastLimbDistance, partialTicks,
				!(lastLimbDistance > -0.15F && lastLimbDistance < 0.15F), Collections.singletonList(entityModelData));
		if (modelProvider instanceof IAnimatableModel) {
			((IAnimatableModel<T>) modelProvider).setCustomAnimations(entityIn, this.getUniqueID(entityIn), predicate);
		}
		Minecraft.getInstance().textureManager.bind(getTextureLocation(entityIn));
		Color renderColor = getRenderColor(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn,
				getTextureLocation(entityIn));
		render(model, entityIn, partialTicks, renderType, matrixStackIn, bufferIn, null, packedLightIn,
				getPackedOverlay(entityIn, 0), (float) renderColor.getRed() / 255f,
				(float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
				(float) renderColor.getAlpha() / 255);
		matrixStackIn.popPose();
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float partialTicks,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		renderEarlyMat = stackIn.last().pose().copy();
		this.animatable = animatable;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, alpha);
	}
	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.last();
			Matrix4f boneMat = entry.pose().copy();

			// Model space
			Matrix4f renderEarlyMatInvert = renderEarlyMat.copy();
			renderEarlyMatInvert.invert();
			Matrix4f modelPosBoneMat = boneMat.copy();
			modelPosBoneMat.multiplyBackward(renderEarlyMatInvert);
			bone.setModelSpaceXform(modelPosBoneMat);

			// Local space
			Matrix4f dispatchedMatInvert = this.dispatchedMat.copy();
			dispatchedMatInvert.invert();
			Matrix4f localPosBoneMat = boneMat.copy();
			localPosBoneMat.multiplyBackward(dispatchedMatInvert);
			// (Offset is the only transform we may want to preserve from the dispatched mat)
			Vector3d renderOffset = this.getRenderOffset(animatable, 1.0F);
			localPosBoneMat.translate(new Vector3f((float) renderOffset.x(), (float) renderOffset.y(), (float) renderOffset.z()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			worldPosBoneMat.translate(new Vector3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
			bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	public static int getPackedOverlay(Entity livingEntityIn, float uIn) {
		return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@AvailableSince(value = "3.0.95")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.95")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.95")
	@Override
	public float getWidthScale(T animatable2) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.95")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getUUID().hashCode();
	}

	protected IRenderTypeBuffer rtb = null;
	
	@Override
	public void setCurrentRTB(IRenderTypeBuffer rtb) {
		this.rtb = rtb;
	}

	@Override
	public IRenderTypeBuffer getCurrentRTB() {
		return this.rtb;
	}

}
