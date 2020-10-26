package software.bernie.geckolib.renderer.geo;

import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.AnimatedGeoModel;

import java.awt.*;

public abstract class GeoBlockRenderer<T extends BlockEntity & IAnimatable> extends BlockEntityRenderer implements IGeoRenderer<T> {
    static {
        AnimationController.addModelFetcher((IAnimatable object) -> {
            if (object instanceof BlockEntity) {
                BlockEntity tile = (BlockEntity) object;
                BlockEntityRenderer<BlockEntity> renderer = BlockEntityRenderDispatcher.INSTANCE.get(tile);
                if (renderer instanceof GeoBlockRenderer)
                {
                    return ((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
                }
            }
            return null;
        });
    }

    private final AnimatedGeoModel<T> modelProvider;

    public GeoBlockRenderer(BlockEntityRenderDispatcher rendererDispatcherIn, AnimatedGeoModel<T> modelProvider) {
        super(rendererDispatcherIn);
        this.modelProvider = modelProvider;
    }

    @Override
    public void render(BlockEntity tile, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
    }

    public void render(T tile, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
        modelProvider.setLivingAnimations(tile, this.getUniqueID(tile));
        stack.push();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0, 0.5);

        //rotateBlock(getFacing(tile), stack);

        MinecraftClient.getInstance().getTextureManager().bindTexture(modelProvider.getTextureLocation(tile));
        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(tile));
        Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn, modelProvider.getTextureLocation(tile));
        render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.pop();
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return this.modelProvider;
    }

    protected void rotateBlock(Direction facing, MatrixStack stack) {
        switch (facing) {
            case SOUTH:
                stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
                break;
            case WEST:
                stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
                break;
            case NORTH:
                stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(0));
                break;
            case EAST:
                stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270));
                break;
            case UP:
                stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
                break;
            case DOWN:
                stack.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(90));
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
}