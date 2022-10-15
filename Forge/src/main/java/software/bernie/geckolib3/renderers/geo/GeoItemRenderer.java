package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;

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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
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

public abstract class GeoItemRenderer<T extends Item & IAnimatable> extends BlockEntityWithoutLevelRenderer
		implements IGeoRenderer<T> {
	// Register a model fetcher for this renderer
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof Item) {
				Item item = (Item) object;
				BlockEntityWithoutLevelRenderer renderer = IClientItemExtensions.of(item).getCustomRenderer();
				if (renderer instanceof GeoItemRenderer) {
					return (IAnimatableModel<Object>) ((GeoItemRenderer<?>) renderer).getGeoModelProvider();
				}
			}
			return null;
		});
	}

	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;
	protected float widthScale;
	protected float heightScale;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;

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

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@AvailableSince(value = "3.1.24")
	protected IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	protected void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.24")
	protected float getHeightScale(T entity) {
		return this.heightScale;
	}

	// fixes the item lighting
	@Override
	public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack,
			MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
		if (p_239207_2_ == ItemTransforms.TransformType.GUI) {
			matrixStack.pushPose();
			MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers()
					.bufferSource();
			Lighting.setupForFlatItems();
			this.render((T) itemStack.getItem(), matrixStack, bufferIn, combinedLightIn, itemStack);
			irendertypebuffer$impl.endBatch();
			RenderSystem.enableDepthTest();
			Lighting.setupFor3DItems();
			matrixStack.popPose();
		} else {
			this.render((T) itemStack.getItem(), matrixStack, bufferIn, combinedLightIn, itemStack);
		}
	}

	public void render(T animatable, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn,
			ItemStack itemStack) {
		this.currentItemStack = itemStack;
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(animatable));
		AnimationEvent itemEvent = new AnimationEvent(animatable, 0, 0, Minecraft.getInstance().getFrameTime(), false,
				Collections.singletonList(itemStack));
		modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
		this.dispatchedMat = stack.last().pose().copy();
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		stack.pushPose();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}
	
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, PoseStack matrixStackIn,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
		IGeoRenderer.super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	@Override
	public void renderEarly(T animatable, PoseStack stackIn, float partialTicks, MultiBufferSource renderTypeBuffer,
			VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float alpha) {
		renderEarlyMat = stackIn.last().pose().copy();
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
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			PoseStack.Pose entry = stack.last();
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
			Vec3 renderOffset = this.getRenderOffset(animatable, 1.0F);
			localPosBoneMat.translate(
					new Vector3f((float) renderOffset.x(), (float) renderOffset.y(), (float) renderOffset.z()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			// Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			// worldPosBoneMat.translate(new Vector3f((float) ((ItemEntity)animatable).getX(), (float) ((ItemEntity)animatable).getY(), (float) ((ItemEntity)animatable).getZ()));
			// bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	public Vec3 getRenderOffset(T pEntity, float pPartialTicks) {
		return Vec3.ZERO;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return GeckoLibUtil.getIDFromStack(currentItemStack);
	}
	
	protected MultiBufferSource rtb = null;

	@Override
	public void setCurrentRTB(MultiBufferSource rtb) {
		this.rtb = rtb;
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return this.rtb;
	}
}
