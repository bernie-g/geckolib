package software.bernie.geckolib.example.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.block.tile.TileEntityJackInTheBox;
import software.bernie.geckolib.example.client.renderer.model.tile.JackInTheBoxModel;
import software.bernie.geckolib.tesr.AnimatedBlockRenderer;

import java.awt.*;

public class JackInTheBoxTileRenderer extends AnimatedBlockRenderer<TileEntityJackInTheBox, JackInTheBoxModel>
{
	private float hue;

	public JackInTheBoxTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new JackInTheBoxModel());
	}

	@Override
	public ResourceLocation getBlockTexture(TileEntityJackInTheBox entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/jackinthebox.png");
	}

	@Override
	protected Color getRenderColor(TileEntityJackInTheBox tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		if(hue >= 1)
		{
			hue = 0;
		}
		else {
			hue += 0.001f;
		}

		Color hsb = Color.getHSBColor(hue, 1, 1);
		return hsb;
	}
}
