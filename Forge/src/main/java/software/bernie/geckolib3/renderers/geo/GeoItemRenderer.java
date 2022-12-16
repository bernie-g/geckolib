package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.geckolib3.util.IRenderCycle;

public abstract class GeoItemRenderer<T extends Item & IAnimatable> extends ItemStackTileEntityRenderer
		implements IGeoRenderer<T> {
	// Register a model fetcher for this renderer
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof Item) {
				Item item = (Item) object;
				ItemStackTileEntityRenderer renderer = item.getItemStackTileEntityRenderer();
				if (renderer instanceof GeoItemRenderer) {
					return (IAnimatableModel<Object>) ((GeoItemRenderer<?>) renderer).getGeoModelProvider();
				}
			}
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

	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return modelProvider;
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

	// fixes the item lighting
	@Override
	public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType p_239207_2_,
			MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int p_239207_6_) {
		if (p_239207_2_ == ItemCameraTransforms.TransformType.GUI) {
			matrixStack.pushPose();
			IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
			RenderHelper.setupForFlatItems();
			this.render((T) itemStack.getItem(), matrixStack, bufferIn, combinedLightIn, itemStack);
			irendertypebuffer$impl.endBatch();
			RenderSystem.enableDepthTest();
			RenderHelper.setupFor3DItems();
			matrixStack.popPose();
		} else {
			this.render((T) itemStack.getItem(), matrixStack, bufferIn, combinedLightIn, itemStack);
		}
	}

	public void render(T animatable, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn,
			ItemStack itemStack) {
		this.currentItemStack = itemStack;
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
		AnimationEvent itemEvent = new AnimationEvent(animatable, 0, 0, Minecraft.getInstance().getFrameTime(), false,
				Collections.singletonList(itemStack));
		modelProvider.setCustomAnimations(animatable, this.getUniqueID(animatable), itemEvent);
		this.dispatchedMat = stack.last().pose().copy();
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		stack.pushPose();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		Minecraft.getInstance().textureManager.bind(getTextureLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float partialTicks, IRenderTypeBuffer renderTypeBuffer,
			IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float alpha) {
		renderEarlyMat = stackIn.last().pose().copy();
		this.animatable = animatable;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
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
			// (Offset is the only transform we may want to preserve from the dispatched
			// mat)
			Vector3d renderOffset = this.getRenderOffset(animatable, 1.0F);
			localPosBoneMat.translate(
					new Vector3f((float) renderOffset.x(), (float) renderOffset.y(), (float) renderOffset.z()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			worldPosBoneMat.translate(new Vector3f((float) Minecraft.getInstance().cameraEntity.getX(),
					(float) Minecraft.getInstance().cameraEntity.getY(),
					(float) Minecraft.getInstance().cameraEntity.getZ()));
			bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
				alpha);
	}

	public Vector3d getRenderOffset(T pEntity, float pPartialTicks) {
		return Vector3d.ZERO;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return GeckoLibUtil.getIDFromStack(currentItemStack);
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
