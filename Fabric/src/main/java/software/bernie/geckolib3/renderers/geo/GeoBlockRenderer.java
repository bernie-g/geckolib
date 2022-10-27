package software.bernie.geckolib3.renderers.geo;

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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;

public abstract class GeoBlockRenderer<T extends BlockEntity & IAnimatable>
		implements IGeoRenderer<T>, BlockEntityRenderer {
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof BlockEntity) {
				BlockEntity tile = (BlockEntity) object;
				BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher()
						.getRenderer(tile);
				if (renderer instanceof GeoBlockRenderer) {
					return (IAnimatableModel<Object>) ((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
				}
			}
			return null;
		});
	}

	protected final AnimatedGeoModel<T> modelProvider;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	protected float widthScale = 1;
	protected float heightScale = 1;

	public GeoBlockRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

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
	public void render(BlockEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(tile));
		modelProvider.setLivingAnimations(tile, this.getUniqueID(tile));

		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.last().pose();
		stack.pushPose();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0, 0.5);

		rotateBlock(getFacing(tile), stack);

		RenderSystem.setShaderTexture(0, getTextureLocation(tile));
		Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn,
				getTextureLocation(tile));
		render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}

	@Override
	public void renderEarly(T animatable, PoseStack stackIn, float partialTicks, MultiBufferSource renderTypeBuffer,
			VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float alpha) {
		renderEarlyMat = stackIn.last().pose();
		this.animatable = animatable;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			PoseStack.Pose entry = stack.last();
			Matrix4f boneMat = entry.pose();

			// Model space
			Matrix4f renderEarlyMatInvert = renderEarlyMat;
			renderEarlyMatInvert.invert();
			Matrix4f modelPosBoneMat = boneMat;
			multiplyBackward(modelPosBoneMat, renderEarlyMatInvert);
			bone.setModelSpaceXform(modelPosBoneMat);

			// Local space
			Matrix4f dispatchedMatInvert = this.dispatchedMat;
			dispatchedMatInvert.invert();
			Matrix4f localPosBoneMat = boneMat;
			multiplyBackward(localPosBoneMat, dispatchedMatInvert);
			// (Offset is the only transform we may want to preserve from the dispatched
			// mat)
			Vec3 renderOffset = this.getPositionOffset(animatable, 1.0F);
			localPosBoneMat.translate(
					new Vector3f((float) renderOffset.x(), (float) renderOffset.y(), (float) renderOffset.z()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			Matrix4f worldPosBoneMat = localPosBoneMat;
			worldPosBoneMat.translate(new Vector3f((float) ((BlockEntity) animatable).getBlockPos().getX(),
					(float) ((BlockEntity) animatable).getBlockPos().getY(),
					(float) ((BlockEntity) animatable).getBlockPos().getZ()));
			bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
				alpha);
	}

	public Vec3 getPositionOffset(T entity, float tickDelta) {
		return Vec3.ZERO;
	}

	public void multiplyBackward(Matrix4f first, Matrix4f other) {
		Matrix4f copy = other;
		copy.mul(first);
		first.mapXnYnZ(copy);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getBlockPos().hashCode();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public float getWidthScale(T animatable2) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	protected void rotateBlock(Direction facing, PoseStack stack) {
		switch (facing) {
		case SOUTH:
			stack.mulPose(Axis.YP.rotationDegrees(180));
			break;
		case WEST:
			stack.mulPose(Axis.YP.rotationDegrees(90));
			break;
		case NORTH:
			stack.mulPose(Axis.YP.rotationDegrees(0));
			break;
		case EAST:
			stack.mulPose(Axis.YP.rotationDegrees(270));
			break;
		case UP:
			stack.mulPose(Axis.XP.rotationDegrees(90));
			break;
		case DOWN:
			stack.mulPose(Axis.XN.rotationDegrees(90));
			break;
		}
	}

	private Direction getFacing(T tile) {
		BlockState blockState = tile.getBlockState();
		if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
			return blockState.getValue(HorizontalDirectionalBlock.FACING);
		} else if (blockState.hasProperty(DirectionalBlock.FACING)) {
			return blockState.getValue(DirectionalBlock.FACING);
		} else {
			return Direction.NORTH;
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		return this.modelProvider.getTextureResource(entity);
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
