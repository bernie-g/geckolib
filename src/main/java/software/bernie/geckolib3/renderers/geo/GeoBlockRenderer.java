package software.bernie.geckolib3.renderers.geo;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@SuppressWarnings({ "unchecked" })
public abstract class GeoBlockRenderer<T extends TileEntity & IAnimatable> extends TileEntitySpecialRenderer<T>
		implements IGeoRenderer<T> {
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof TileEntity) {
				TileEntity tile = (TileEntity) object;
				TileEntitySpecialRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance
						.getRenderer(tile);
				if (renderer instanceof GeoBlockRenderer) {
					return (IAnimatableModel<Object>) ((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
				}
			}
			return null;
		});
	}

	private final AnimatedGeoModel<T> modelProvider;

	public GeoBlockRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		this.render(te, x, y, z, partialTicks, destroyStage);
	}

	public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(tile));
		modelProvider.setLivingAnimations(tile, this.getUniqueID(tile));

		int light = tile.getWorld().getCombinedLight(tile.getPos(), 0);
		int lx = light % 65536;
		int ly = light / 65536;

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		OpenGlHelper.setLightmapTextureCoords(GL11.GL_TEXTURE_2D, lx, ly);
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(0, 0.01f, 0);
		GlStateManager.translate(0.5, 0, 0.5);

		rotateBlock(getFacing(tile));

		Minecraft.getMinecraft().renderEngine.bindTexture(getTextureLocation(tile));
		Color renderColor = getRenderColor(tile, partialTicks);
		render(model, tile, partialTicks, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		GlStateManager.popMatrix();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	protected void rotateBlock(EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			GlStateManager.rotate(180, 0, 1, 0);
			break;
		case WEST:
			GlStateManager.rotate(90, 0, 1, 0);
			break;
		case NORTH:
			/* There is no need to rotate by 0 */
			break;
		case EAST:
			GlStateManager.rotate(270, 0, 1, 0);
			break;
		case UP:
			GlStateManager.rotate(90, 1, 0, 0);
			break;
		case DOWN:
			GlStateManager.rotate(90, -1, 0, 0);
			break;
		}
	}

	private EnumFacing getFacing(T tile) {
		IBlockState blockState = tile.getWorld().getBlockState(tile.getPos());

		if (blockState.getPropertyKeys().contains(BlockHorizontal.FACING)) {
			return blockState.getValue(BlockHorizontal.FACING);
		} else if (blockState.getPropertyKeys().contains(BlockDirectional.FACING)) {
			return blockState.getValue(BlockDirectional.FACING);
		} else {
			return EnumFacing.NORTH;
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}
}
