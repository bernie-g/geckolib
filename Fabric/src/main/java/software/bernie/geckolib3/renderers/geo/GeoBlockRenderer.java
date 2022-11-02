package software.bernie.geckolib3.renderers.geo;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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
				BlockEntityRenderer<BlockEntity> renderer = MinecraftClient.getInstance()
						.getBlockEntityRenderDispatcher().get(tile);
				if (renderer instanceof GeoBlockRenderer) {
					return (IAnimatableModel<Object>) ((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
				}
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

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

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

	public GeoBlockRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(BlockEntity tile, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T tile, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn,
			int packedLightIn) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(tile));
		modelProvider.setLivingAnimations(tile, this.getUniqueID(tile));
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.peek().getPositionMatrix().copy();
		stack.push();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0, 0.5);

		rotateBlock(getFacing(tile), stack);

		MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(tile));
		Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderLayer renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn,
				getTextureLocation(tile));
		render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.pop();
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float partialTicks,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		renderEarlyMat = stackIn.peek().getPositionMatrix().copy();
		this.animatable = animatable;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.peek();
			Matrix4f boneMat = entry.getPositionMatrix().copy();

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
			// (Offset is the only transform we may want to preserve from the dispatched
			// mat)
			Vec3d renderOffset = this.getPositionOffset(animatable, 1.0F);
			localPosBoneMat.addToLastColumn(
					new Vec3f((float) renderOffset.getX(), (float) renderOffset.getY(), (float) renderOffset.getZ()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			worldPosBoneMat.addToLastColumn(new Vec3f((float) ((BlockEntity) animatable).getPos().getX(),
					(float) ((BlockEntity) animatable).getPos().getY(),
					(float) ((BlockEntity) animatable).getPos().getZ()));
			bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
				alpha);
	}

	public Vec3d getPositionOffset(T entity, float tickDelta) {
		return Vec3d.ZERO;
	}

	public void multiplyBackward(Matrix4f first, Matrix4f other) {
		Matrix4f copy = other.copy();
		copy.multiply(first);
		first.load(copy);
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
		return animatable.getPos().hashCode();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getWidthScale(T animatable2) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	protected void rotateBlock(Direction facing, MatrixStack stack) {
		switch (facing) {
		case SOUTH:
			stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
			break;
		case WEST:
			stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
			break;
		case NORTH:
			stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(0));
			break;
		case EAST:
			stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
			break;
		case UP:
			stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
			break;
		case DOWN:
			stack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));
			break;
		}
	}

	private Direction getFacing(T tile) {
		BlockState blockState = tile.getCachedState();
		if (blockState.contains(HorizontalFacingBlock.FACING)) {
			return blockState.get(HorizontalFacingBlock.FACING);
		} else if (blockState.contains(FacingBlock.FACING)) {
			return blockState.get(FacingBlock.FACING);
		} else {
			return Direction.NORTH;
		}
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
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
