package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus.AvailableSince;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nonnull;

public abstract class GeoBlockRenderer<T extends BlockEntity & IAnimatable>
		implements IGeoRenderer<T>, BlockEntityRenderer {
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof BlockEntity tile) {
				BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher()
						.getRenderer(tile);

				if (renderer instanceof GeoBlockRenderer blockRenderer)
					return (IAnimatableModel<Object>)blockRenderer.getGeoModelProvider();
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
	protected MultiBufferSource rtb = null;

	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoBlockRenderer(BlockEntityRendererProvider.Context rendererProvider,
			AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
							VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
							float alpha) {
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.animatable = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void render(BlockEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
			int packedLight, int packedOverlay) {
		render((T)tile, partialTicks, poseStack, bufferSource, packedLight);
	}

	public void render(T tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(tile));
		modelProvider.setLivingAnimations(tile, getInstanceId(tile)); // TODO change to setCustomAnimations in 1.20+
		this.dispatchedMat = poseStack.last().pose().copy();
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.pushPose();
		poseStack.translate(0, 0.01f, 0);
		poseStack.translate(0.5, 0, 0.5);

		rotateBlock(getFacing(tile), poseStack);

		RenderSystem.setShaderTexture(0, getTextureLocation(tile));
		Color renderColor = getRenderColor(tile, partialTick, poseStack, bufferSource, null, packedLight);
		RenderType renderType = getRenderType(tile, partialTick, poseStack, bufferSource, null, packedLight,
				getTextureLocation(tile));
		render(model, tile, partialTick, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.NO_OVERLAY,
				renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
				renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
		poseStack.popPose();
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
								  int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);
			BlockPos pos = this.animatable.getBlockPos();

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.translate(new Vector3f(getRenderOffset(this.animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.translate(new Vector3f(pos.getX(), pos.getY(), pos.getZ()));
			bone.setWorldSpaceXform(worldState);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	public Vec3 getRenderOffset(T animatable, float partialTick) {
		return Vec3.ZERO;
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
		return animatable.getBlockPos().hashCode();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
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

	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		switch (facing) {
			case SOUTH -> poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
			case WEST -> poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
			case NORTH -> poseStack.mulPose(Vector3f.YP.rotationDegrees(0));
			case EAST -> poseStack.mulPose(Vector3f.YP.rotationDegrees(270));
			case UP -> poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
			case DOWN -> poseStack.mulPose(Vector3f.XN.rotationDegrees(90));
		}
	}

	private Direction getFacing(T tile) {
		BlockState blockState = tile.getBlockState();
		if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
			return blockState.getValue(HorizontalDirectionalBlock.FACING);
		}
		else if (blockState.hasProperty(DirectionalBlock.FACING)) {
			return blockState.getValue(DirectionalBlock.FACING);
		}
		else {
			return Direction.NORTH;
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		return this.modelProvider.getTextureLocation(animatable);
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
