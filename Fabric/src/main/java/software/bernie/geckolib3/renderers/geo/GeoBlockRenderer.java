package software.bernie.geckolib3.renderers.geo;

import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

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
	public Integer getUniqueID(T animatable) {
		return animatable.getPos().hashCode();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
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
