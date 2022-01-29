package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@SuppressWarnings({ "unchecked", "rawtypes" })
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

	private final AnimatedGeoModel<T> modelProvider;

	public GeoBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn,
			AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(BlockEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(tile));
		modelProvider.setLivingAnimations(tile, this.getUniqueID(tile));
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
	public Integer getUniqueID(T animatable) {
		return animatable.getBlockPos().hashCode();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	protected void rotateBlock(Direction facing, PoseStack stack) {
		switch (facing) {
		case SOUTH:
			stack.mulPose(Vector3f.YP.rotationDegrees(180));
			break;
		case WEST:
			stack.mulPose(Vector3f.YP.rotationDegrees(90));
			break;
		case NORTH:
			stack.mulPose(Vector3f.YP.rotationDegrees(0));
			break;
		case EAST:
			stack.mulPose(Vector3f.YP.rotationDegrees(270));
			break;
		case UP:
			stack.mulPose(Vector3f.XP.rotationDegrees(90));
			break;
		case DOWN:
			stack.mulPose(Vector3f.XN.rotationDegrees(90));
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
		return this.modelProvider.getTextureLocation(instance);
	}
}
