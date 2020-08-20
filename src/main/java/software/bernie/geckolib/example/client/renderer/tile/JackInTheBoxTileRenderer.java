package software.bernie.geckolib.example.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.block.tile.TileEntityJackInTheBox;
import software.bernie.geckolib.example.client.renderer.model.tile.JackInTheBoxModel;
import software.bernie.geckolib.tesr.AnimatedBlockRenderer;

public class JackInTheBoxTileRenderer extends AnimatedBlockRenderer<TileEntityJackInTheBox, JackInTheBoxModel>
{
	public JackInTheBoxTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new JackInTheBoxModel());
	}

	@Override
	public ResourceLocation getBlockTexture(TileEntityJackInTheBox entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/jackinthebox.png");
	}
}
