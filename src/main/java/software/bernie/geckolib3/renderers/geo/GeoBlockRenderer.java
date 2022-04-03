package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class GeoBlockRenderer<T extends TileEntity & IAnimatable> extends TileEntityRenderer
		implements IGeoRenderer<T> {
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof TileEntity) {
				TileEntity tile = (TileEntity) object;
				TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
				if (renderer instanceof GeoBlockRenderer) {
					return (IAnimatableModel<Object>) ((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
				}
			}
			return null;
		});
	}

	private final AnimatedGeoModel<T> modelProvider;

	public GeoBlockRenderer(TileEntityRendererDispatcher rendererDispatcherIn, AnimatedGeoModel<T> modelProvider) {
		super(rendererDispatcherIn);
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(TileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(tile));
		modelProvider.setLivingAnimations(tile, this.getUniqueID(tile));
		stack.pushPose();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0, 0.5);

		rotateBlock(getFacing(tile), stack);

		Minecraft.getInstance().textureManager.bind(getTextureLocation(tile));
		Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn,
				getTextureLocation(tile));
		render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	protected void rotateBlock(Direction facing, MatrixStack stack) {
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
		if (blockState.hasProperty(HorizontalBlock.FACING)) {
			return blockState.getValue(HorizontalBlock.FACING);
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
